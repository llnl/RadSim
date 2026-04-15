"""
Configure the CLASSPATH and start up JPype.

Depended on project's file structure
If the location of the jars dictory has change this script must be updated
the DEVEL variable accordingly

One environment variable need to be set first:
    JAVA_HOME (pointing to the same JDK that JPype was compiled against)
"""

import os
import glob
import platform
import jpype
import jpype.imports
import pathlib
import sys


def javaClasspath():
    """
    Set the classpath for unix or windows.
    """
    DEVEL = str(pathlib.Path(__file__).parents[1])+'/src'
    classes = []
    for repo in (
            'gov.llnl.utility',
            'gov.llnl.math',
            'gov.llnl.rtk',
            'gov.bnl.nndc.ensdf',
            'gov.nist.physics.xray',
            'gov.nist.physics.n42',
            'gov.nist.physics.xcom',
            'gov.llnl.rtk.mcnp',
            'gov.llnl.rtk.geant4',
            'gov.llnl.rtk.response'
    ):
        path = glob.glob(os.path.join(DEVEL, repo, 'dist', '*.jar'))
        if not path:
            print("%s not found" % repo)
            continue
        classes.append(path[0])

    sep = ':'
    if platform.system() == 'Windows' or 'CYGWIN' in platform.system():
        sep = ';'

    return sep.join(classes)


jarFiles = javaClasspath()
try:
    if not jpype.isJVMStarted():
        jpype.startJVM(jpype.getDefaultJVMPath(),
                       '-Djava.class.path=%s' % jarFiles, convertStrings=False)
except:
    print("This project only works with the proper Java bin and JAVA_HOME environment variable.")
    print("Check that you have both Java JDK installed and JAVA_HOME set correctly and try again.")
    print("JAVA_HOME must point to the same JDK that JPype was compiled against.")
    print("Class path includes: ")
    if ':' in jarFiles:
        jarFiles = jarFiles.split(':')
    else:
        jarFiles = jarFiles.split(';')
    for item in jarFiles:
        print(item)

    print("Exiting.")
    sys.exit(1)
