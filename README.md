# General dataset and library setup

## How to set-up the C++ Project
The C++ project contains the actual code for GCEA and the querying for Virtuoso, plus some scripts required to prepare the data for the input. Mainly, this project requires to both install the Boost library and to set-up Virtuoso.

## How to set-up Virtuoso.
The process of the Virtuoso Setup is described by the script `virtuoso_dependencies.sh`, which will install all the dependencies and libraries required to connect the C++ code to the actual Virtuoso Driver. After doing that, we need to set-up the ODBC connections that are exploited by the C++ libraries.

If your system has the same default directory paths as Ubuntu (GNU/Linux), then the user can run the `cpfiles.sh` script in the `odbc_setup` folder to copy the odbc connection drivers to the right and expected locations. After editing the file `virtuoso_setup/virtuoso.ini` accordingly to your hardware specification, the script `virt_start.sh` in the same folder will start the Virtuoso Server. Now, the Virtuoso server should be receiving binary/ODBC connections on the 1111 port, while the web browser interface should be provided on http://localhost:8890/. The default username and password for accessing the Virtuoso Conductor are both `dba`, for DataBase Administrator.

## How to set-up the Java Project
The Java project in the `usergenerator` folder does not require any additional tool set-up a part from Maven 3, which will automatically download all the dependencies and libraries required for the project. Some of the Java scripts require to set-up the C++ project first.

## How to recreate the graph operands (Syntetic Networks)
Run the java class `GCEA.DatasetSampleGenerator` from the java project in the `usergenerator` folder. This class automates the operations for generating the different operands and subgraphs from a single adjacency list dataset. This process will perform the following operations in sequence:
