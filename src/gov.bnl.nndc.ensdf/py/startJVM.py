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
    DEVEL = str(pathlib.Path(__file__).parents[2])
    print(DEVEL)
    classes = []
    for repo in (
            'gov.llnl.utility',
            'gov.llnl.math',
            'gov.llnl.rtk',
            'gov.llnl.rtk.decay',
            'gov.bnl.nndc.ensdf',
            'gov.nist.xray',
    ):
        path = glob.glob(os.path.join(DEVEL, repo, 'dist', '*.jar'))
        if not path:
            print("%s not found" % repo)
            continue
        classes.append(path[0])

    # also add external dependencies:
    classes.extend(glob.glob(os.path.join(
        DEVEL, 'gov.llnl.sea', 'dependency', '*.jar')))

    sep = ':'
    if platform.system() == 'Windows' or 'CYGWIN' in platform.system():
        sep = ';'

    return sep.join(classes)


jarFiles = javaClasspath()
try:
    if not jpype.isJVMStarted():
        jpype.startJVM(jpype.getDefaultJVMPath(),
                        # "-Xint", "-Xdebug", "-Xnoagent", "-Xrunjdwp:transport=dt_socket,server=y,address=12999,suspend=n",
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
