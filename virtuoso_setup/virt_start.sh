#!/bin/bash
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/virtuoso-opensource/lib/
export PATH=$PATH:/usr/local/virtuoso-opensource/bin/
sudo /usr/local/virtuoso-opensource/bin/virtuoso-t -df +configfile virtuoso.ini
