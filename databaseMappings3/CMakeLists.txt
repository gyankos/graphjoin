cmake_minimum_required(VERSION 3.10)
project(databaseMappings3)

set(CMAKE_CXX_STANDARD 17)

#############
### Boost ###
#############
find_package( Boost REQUIRED COMPONENTS graph serialization )
find_package (Threads)
set(Boost_USE_MULTITHREADED OFF)


####################################
### LibRDF/Redland with Virtuoso ###
####################################
find_package(PkgConfig)
set( ENV{PKG_CONFIG_PATH} "$ENV{PKG_CONFIG_PATH}:/usr/local/lib/pkgconfig" )
LIST(INSERT CMAKE_MODULE_PATH 0 "/opt/local/include/db48")
pkg_search_module(RASQAL2 REQUIRED rasqal)
pkg_search_module(REDLAND REQUIRED redland)

INCLUDE_DIRECTORIES( ${Boost_INCLUDE_DIRS})

add_executable(reindexing        indexing.cpp
                                 serializer/HashIndexArrayField.h serializer/mappers.cpp serializer/mappers.h serializer/serializers.cpp serializer/serializers.h serializer/VAOffsetStructures.h serializer/VertexIndexArrayField.h serializer/entry.cpp serializer/entry.h)

add_executable(print_graph       print_graph.cpp
                                 hashing/hashing.cpp hashing/hashing.h serializer/HashIndexArrayField.h serializer/mappers.cpp serializer/mappers.h serializer/serializers.cpp serializer/serializers.h serializer/VAOffsetStructures.h serializer/VertexIndexArrayField.h serializer/entry.cpp serializer/entry.h)

find_package(OpenMP)
add_executable(serializer        serializer.cpp
                                 hashing/hashing.cpp hashing/hashing.h serializer/csvSerialize.cpp serializer/csvSerialize.h serializer/HashIndexArrayField.h serializer/mappers.cpp serializer/mappers.h serializer/serializers.cpp serializer/serializers.h serializer/VAOffsetStructures.h serializer/VertexIndexArrayField.h serializer/entry.cpp serializer/entry.h)
target_link_libraries(serializer ${CMAKE_THREAD_LIBS_INIT} OpenMP::OpenMP_CXX)

add_executable(graph_sampler        graph_sampler.cpp BitArray1D.cpp BitArray1D.h)
TARGET_LINK_LIBRARIES(graph_sampler ${Boost_LIBRARIES})

add_executable(graph_join           main.cpp
                                    serializer/mappers.h serializer/mappers.cpp operations/eqjoin/eq_join_proposed.cpp operations/utils/query_chunk.cpp operations/utils/query_chunk.h operations/utils/query.cpp operations/utils/query.h operations/utils/store_join_results.cpp operations/utils/store_join_results.h operations/eqjoin/eq_join_proposed.h)

add_executable(graph_statistics     statistics.cpp)

add_executable(virtuoso_loader      virtuoso_loader.cpp virtuoso/test.cpp virtuoso/Virtuoso.cpp virtuoso/VirtuosoGraph.cpp virtuoso/VirtuosoQuery.cpp virtuoso/utils/SchemaValues.cpp virtuoso/utils/triplets.c virtuoso/SampleVirtuoso.cpp)
TARGET_LINK_LIBRARIES(virtuoso_loader ${RASQAL2_LIBRARIES} ${REDLAND_LIBRARIES})
target_include_directories(virtuoso_loader PUBLIC ${RASQAL2_INCLUDE_DIRS})
target_include_directories(virtuoso_loader PUBLIC ${REDLAND_INCLUDE_DIRS})
target_compile_options(virtuoso_loader PUBLIC ${RASQAL2_CFLAGS_OTHER} ${REDLAND_CFLAGS_OTHER})

add_executable(virtuoso_joiner      virtuoso_joiner.cpp virtuoso/test.cpp virtuoso/Virtuoso.cpp virtuoso/VirtuosoGraph.cpp virtuoso/VirtuosoQuery.cpp virtuoso/utils/SchemaValues.cpp virtuoso/utils/triplets.c virtuoso/SampleVirtuoso.cpp)
TARGET_LINK_LIBRARIES(virtuoso_joiner ${RASQAL2_LIBRARIES} ${REDLAND_LIBRARIES})
target_include_directories(virtuoso_joiner PUBLIC ${RASQAL2_INCLUDE_DIRS})
target_include_directories(virtuoso_joiner PUBLIC ${REDLAND_INCLUDE_DIRS})
target_compile_options(virtuoso_joiner PUBLIC ${RASQAL2_CFLAGS_OTHER} ${REDLAND_CFLAGS_OTHER})