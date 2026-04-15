import startJVM
import jpype
import jpype.imports
import numpy as np
import matplotlib.pyplot as plt

from gov.llnl.rtk.physics import Nuclides
from gov.llnl.rtk.physics import SourceImpl
from gov.llnl.rtk.physics import Quantity
from gov.llnl.rtk.physics import EmissionCalculator
from gov.bnl.nndc.ensdf.decay import BNLDecayLibrary
from gov.sandia.gadras.decay import DecayLibrary
from gov.nist.physics.xray import NISTXrayLibrary
from gov.llnl.rtk.physics import DecayCalculator

from java.nio.file import Paths
from java.util import ArrayList

bnllib = BNLDecayLibrary()
bnllib.setXrayLibrary(NISTXrayLibrary.getInstance())
bnllib.loadFile(Paths.get("BNL2023.txt"))
sandialib = DecayLibrary.getInstance()

problems = ['C10', 'O14', 'Ne18', 'Na21']

bnlemcal = EmissionCalculator()
bnlemcal.setDecayLibrary(bnllib)

sandiaemcal = EmissionCalculator()
sandiaemcal.setDecayLibrary(sandialib)

from gov.llnl.rtk.physics import NuclideImpl

import xml
import xml.etree.ElementTree as ET

tree = ET.parse('/Users/hangal1/Downloads/nuclides.xml')
root = tree.getroot()

for i,child in enumerate(root):
    #if(i>100):continue
    if('nuclide' in child.tag):
        print(child.attrib['symbol'])
        #if(child.attrib['symbol'] in problems): continue
        sourceList = ArrayList()
        nuclide = child.attrib['symbol']
        if(Nuclides.get(nuclide).getDecayConstant() < 1e-20): continue
        activity = Quantity.of(1,"Ci")
        gamma_record_bnl = {'E':[], 'I': []}
        gamma_record_sandia = {'E':[], 'I': []}
        try:
            nuclide = SourceImpl.fromActivity(Nuclides.get(nuclide), activity)
            sourceList.add(nuclide)
            bnlout = bnlemcal.apply(sourceList)
            sandiaout = sandiaemcal.apply(sourceList)
        except jpype.JException as ex:
            print(ex.stacktrace())
            raise ex
        for emission in bnlout.getGammas():
            #print("bnl: ",emission.getEnergy().getValue(),emission.getIntensity().getValue())
            gamma_record_bnl['E'].append(emission.getEnergy().getValue())
            gamma_record_bnl['I'].append(emission.getIntensity().getValue())
        for emission in sandiaout.getGammas():
            #print("sandia: ",emission.getEnergy().getValue(),emission.getIntensity().getValue())
            gamma_record_sandia['E'].append(emission.getEnergy().getValue())
            gamma_record_sandia['I'].append(emission.getIntensity().getValue())
        e_values_bnl = gamma_record_bnl['E']
        i_values_bnl = gamma_record_bnl['I']
        e_values_sandia = gamma_record_sandia['E']
        i_values_sandia = gamma_record_sandia['I']
        if(len(e_values_bnl)<1 and len(e_values_sandia)<1): continue
        plt.clf()
        plt.vlines(e_values_bnl, ymin=0, ymax=i_values_bnl, color='blue', linewidth=3, label="BNL", alpha=0.7)
        plt.vlines(e_values_sandia, ymin=0, ymax=i_values_sandia, color='orange', linewidth=3, linestyle='--', label="Sandia", alpha=0.7)
        plt.xlabel('Energy (keV)')
        plt.ylabel('Intensity')
        plt.yscale('log')
        plt.title(child.attrib['symbol']+" (1 Ci)")
        plt.legend()
        plt.savefig('figures/decay_results/'+child.attrib['symbol']+'.png')