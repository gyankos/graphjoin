//
// Created by giacomo on 28/07/19.
//

#include <algorithm>
#include <chrono>
#include "bulk_fusion.h"
#include "../../serializer/serializers.h"

int main(int argc, char* argv[]) {
    std::vector<std::string> args{"1", "2", "3", "4", "5"};
    std::string bulk_1 = "0";
    for (std::string& bulk_2 : args) {
        unsigned long bulk_1_size, bulk_2_size;
        int fd1, fd2;
        JOINRESULT* file1 = (JOINRESULT*)mmapFile(bulk_1, &bulk_1_size, &fd1);
        JOINRESULT* file2 = (JOINRESULT*)mmapFile(bulk_2, &bulk_2_size, &fd2);
        bulk_1_size = (bulk_1_size) / sizeof(JOINRESULT);
        bulk_2_size = (bulk_2_size) / sizeof(JOINRESULT);

        std::string csvVertices = bulk_1+"_"+bulk_2;
        std::string tmpFile = csvVertices + "_VAOffset.bin";
        FILE *table = fopen(tmpFile.c_str(), "w");
        tmpFile = csvVertices + "_Index.bin";
        FILE *secondaryIndex = fopen(tmpFile.c_str(), "w");

        ADJLIST adjList;

        unsigned long long currentVertex = 0;
        unsigned long long currentVertexDstEdge = 0;

        // Reading first file
        auto start = std::chrono::high_resolution_clock::now();
        for (unsigned long i = 0; i<bulk_1_size; i++) {
            if (file1[i].isVertex) {
                currentVertex = file1[i].left;
                currentVertex *= pow(10.0, 9.0);
                currentVertex += file1[i].right;
            } else {
                currentVertexDstEdge = file1[i].left;
                currentVertex *= pow(10.0, 9.0);
                currentVertex += file1[i].right;
                adjList[currentVertex].emplace_back(currentVertexDstEdge);
            }
        }
        // Reading second file
        for (unsigned long i = 0; i<bulk_2_size; i++) {
            if (file2[i].isVertex) {
                currentVertex = file2[i].left;
                currentVertex *= pow(10.0, 9.0);
                currentVertex += file2[i].right;
            } else {
                currentVertexDstEdge = file2[i].left;
                currentVertex *= pow(10.0, 9.0);
                currentVertex += file2[i].right;
                adjList[currentVertex].emplace_back(currentVertexDstEdge);
            }
        }
        auto finishLoad = std::chrono::high_resolution_clock::now();

        // 2. Creating the indexed graph
        const ADJLIST::iterator &it = adjList.begin();
        const ADJLIST::iterator &end = adjList.end();
        unsigned long VAOffset = 0;
        unsigned long HashOffset;
        std::vector<std::string> noValues;
        while (it != end) {
            HashOffset = 0;
            unsigned long long id = it->first;
            std::vector<EDGES_OUTIN> out;
            for (unsigned long long& dst : it->second) {
                out.emplace_back(0, dst);
            }
            std::sort(out.begin(), out.end());
            serialize_vertex_id(secondaryIndex, id, 0, VAOffset);
            serialize_vertex_values(table, &VAOffset, &HashOffset, id, 0, noValues, out);
        }
        auto finishIndex = std::chrono::high_resolution_clock::now();

        std::chrono::duration<double> loadingTime = finishLoad - start;
        std::chrono::duration<double> d = finishIndex - start;

        std::cout << loadingTime.count() << " --> " << d.count() << std::endl;

        fclose(secondaryIndex);
        fclose(table);
        close(fd1);
        close(fd2);
    }
}