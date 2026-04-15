/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.nist.physics.n42.writer;

import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;
import gov.nist.physics.n42.N42Package;
import gov.nist.physics.n42.data.Spectrum;

/**
 *
 * @author her1
 */
public class SpectrumWriter extends ObjectWriter<Spectrum>
{
  public SpectrumWriter()
  {
    super(Options.NONE, "Spectrum", N42Package.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, Spectrum object) throws WriterException
  {
    // Register id
    attributes.setId(object.getId());

    if (object.getDetector() != null)
    {
      String detectorRef = WriterUtilities.getObjectReference(object.getDetector(), getContext());
      attributes.add("radDetectorInformationReference", detectorRef);
    }

    if (object.getEnergyCalibration() != null)
    {
      String energyCalibRef = WriterUtilities.getObjectReference(object.getEnergyCalibration(), getContext());
      attributes.add("energyCalibrationReference", energyCalibRef);
    }

    if (object.getFwhmCalibration() != null)
    {
      String fwhmCalibRef = WriterUtilities.getObjectReference(object.getFwhmCalibration(), getContext());
      attributes.add("FWHMCalibrationReference", fwhmCalibRef);
    }

    if (object.getFullEnergyPeakEfficiencyCalibration() != null)
    {
      String fullEnergyRef = WriterUtilities.getObjectReference(object.getFullEnergyPeakEfficiencyCalibration(), getContext());
      attributes.add("fullEnergyPeakEfficiencyCalibrationReference", fullEnergyRef);
    }

    if (object.getIntrinsicSingleEscapePeakEfficiencyCalibration() != null)
    {
      String singleEnergyRef = WriterUtilities.getObjectReference(object.getIntrinsicSingleEscapePeakEfficiencyCalibration(), getContext());
      attributes.add("intrinsicSingleEscapePeakEfficiencyCalibrationReference", singleEnergyRef);
    }

    if (object.getIntrinsicDoubleEscapePeakEfficiencyCalibration() != null)
    {
      String doubleEnergyRef = WriterUtilities.getObjectReference(object.getIntrinsicDoubleEscapePeakEfficiencyCalibration(), getContext());
      attributes.add("intrinsicDoubleEscapePeakEfficiencyCalibrationReference", doubleEnergyRef);
    }

    if (object.getIntrinsicFullEnergyPeakEfficiencyCalibration() != null)
    {
      String intrFullEnergyRef = WriterUtilities.getObjectReference(object.getIntrinsicFullEnergyPeakEfficiencyCalibration(), getContext());
      attributes.add("intrinsicFullEnergyPeakEfficiencyCalibrationReference", intrFullEnergyRef);
    }

    // FIXME
    // attributes.add("radRawSpectrumReferences", "");
  }

  @Override
  public void contents(Spectrum object) throws WriterException
  {
    WriterBuilder builder = newBuilder();
    WriterUtilities.writeRemarkContents(builder, object);

    builder.element("LiveTimeDuration")
            .writer(new DurationWriter())
            .put(object.getLiveTimeDuration());

    builder.writer(new ChannelDataWriter()).put(object.getCountData());

    if (!object.getExtensions().isEmpty())
    {
      for (Object obj : object.getExtensions())
      {
        try
        {
          ObjectWriter<? extends Object> writer = ObjectWriter.create(obj.getClass());
          builder.writer(writer).put(obj);
        }
        catch (WriterException ext)
        {
          System.out.println("No extension writer for " + obj.getClass());
        }
      }

    }
    // SpectrumExtension
  }
}
