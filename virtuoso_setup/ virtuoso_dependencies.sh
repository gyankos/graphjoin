#!/bin/bash
mkdir downloads
cd downloads
wget http://download.librdf.org/source/rasqal-0.9.33.tar.gz
tar -xvzf rasqal-0.9.33.tar.gz
wget http://download.librdf.org/source/redland-1.0.17.tar.gz
tar -xvzf redland-1.0.17.tar.gz
wget http://download.librdf.org/source/raptor2-2.0.15.tar.gz
tar -xvzf raptor2-2.0.15.tar.gz
git clone https://github.com/openlink/virtuoso-opensource
cd virtuoso-opensource
autoreconf -if
sudo apt-get install -y bison flex gperf gtk-doc-tools libxml2-dev unixodbc unixodbc-dev libcurl4-openssl-dev libxslt-dev libyajl-dev uuid-dev libgmp-dev libssl1.0-dev gawk 
./configure
make
sudo make install
cd ..
cd raptor2-2.0.15
./autogen.sh
make
sudo make install
cd ..
cd rasqal-0.9.33
./autogen.sh
make
sudo make install
cd ..
cd redland-1.0.17
./autogen.sh
./configure --with-virtuoso=yes --with-unixodbc
make
sudo make install
sudo cp /usr/local/include/raptor2/raptor2.h /usr/local/include/raptor2.h
sudo cp /usr/local/include/raptor2/raptor.h /usr/local/include/raptor.h
sudo cp /usr/local/include/rasqal/rasqal.h /usr/local/include/rasqal.h
