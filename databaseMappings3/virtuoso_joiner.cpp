//
// Created by giacomo on 15/12/18.
//

#include <string>
#include <sstream>
#include <iostream>
#include <vector>
#include <algorithm>
#include "virtuoso/utils/SchemaValues.h"
#include "virtuoso/Virtuoso.h"

int main(int argc, char** argv) {

    std::string argsizes{argv[1]};
    std::vector<std::string> sizes;
    {
        std::string token;
        std::istringstream tokenStream(argsizes);
        while (std::getline(tokenStream, token, ',')) {
            sizes.push_back((token.c_str()));
        }
        std::sort(sizes.begin(), sizes.end());

        for (std::string &graphName : sizes) {
            std::string leftPath = GRAPH_BASE_IRI((graphName +"L/"));
            std::string rightPath = GRAPH_BASE_IRI((graphName +"R/"));
            std::string qFirst{"CONSTRUCT { \n"
                               "\t?newSrc <http://jackbergus.alwaysdata.net/graph> \"Result\";\n"
                               "\t      <http://jackbergus.alwaysdata.net/edges/result> ?newDst. \n"
                               "\t?newDst <http://jackbergus.alwaysdata.net/graph> \"Result\".\n"
                               "} \n"
                               /*"FROM NAMED <"};
            qFirst += leftPath;
            qFirst += ">\n"
                      "FROM NAMED <";
            qFirst += rightPath;
            qFirst += ">\n"*/
                               "WHERE\n"
                               "{\n"
                               "  GRAPH ?g { \n"
                               "  \t\t?src1 <http://jackbergus.alwaysdata.net/property/Id> ?id1;\n"
                               "\t      <http://jackbergus.alwaysd0ata.net/property/company1> ?org1;\n"
                               "\t      <http://jackbergus.alwaysdata.net/property/dob1> ?y1.\n"
                               "  \t}.\n"
                               "  GRAPH ?h { \n"
                               "  \t\t?src2 <http://jackbergus.alwaysdata.net/property/Id> ?id2;\n"
                               "\t      <http://jackbergus.alwaysdata.net/property/company1> ?org2;\n"
                               "\t      <http://jackbergus.alwaysdata.net/property/dob1> ?y2.\n"
                               "  \t}\n"
                               "  filter(?g=<"};
            qFirst += leftPath;
            qFirst += "> && \n"
                      "         ?h=<";
            qFirst += rightPath;
            qFirst += "> &&\n"
                      "         ( ?org1 = ?org2 ) && ( ?y1 =  ?y2 ))\n"
                      "         \n"
                      "  BIND (URI(CONCAT(\"http://jackbergus.alwaysdata.net/values/\",?id1,\"-\",?id2)) AS ?newSrc)\n"
                      "  \n"
                      "  OPTIONAL {\n"
                      "  \t\tGRAPH ?g { \n"
                      "  \t\t\t?src1 <http://jackbergus.alwaysdata.net/edges/edge> ?dst1.\n"
                      "  \t\t\t?dst1 <http://jackbergus.alwaysdata.net/property/Id> ?id3;\n"
                      "\t      <http://jackbergus.alwaysdata.net/property/company1> ?org3;\n"
                      "\t      <http://jackbergus.alwaysdata.net/property/dob1> ?y3.\n"
                      "  \t\t}.\n"
                      "\t\tGRAPH ?h { \n"
                      "\t\t\t?src2 <http://jackbergus.alwaysdata.net/edges/edge> ?dst2.\n"
                      "\t\t\t?dst2 <http://jackbergus.alwaysdata.net/property/Id> ?id4;\n"
                      "\t      <http://jackbergus.alwaysdata.net/property/company1> ?org4;\n"
                      "\t      <http://jackbergus.alwaysdata.net/property/dob1> ?y4.\n"
                      "  \t\t}\n"
                      "\t\tFILTER ( ( ?org3 = ?org4 ) && ( ?y3 = ?y4 ) )\n"
                      "\t\tBIND (URI(CONCAT(\"http://jackbergus.alwaysdata.net/values/\",?id3,\"-\",?id4)) AS ?newDst)\n"
                      "\t}\n"
                      "}";
            std::cout << qFirst << std::endl;
            exit(1);


           Virtuoso virt{};
            VirtuosoQuery* q = virt.compileQuery(qFirst);
            clock_t  t = clock();
            if (q->operator()()) {
                t = clock() - t;
                std::cout << graphName << "," << ((double)((double)(clock() - t))/(CLOCKS_PER_SEC / 1000.0)) << std::endl;
            }
           /**/

        }
    }
}