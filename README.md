# CS-StarSchemaHIN

This repository contains Java codes and datasets for the paper:

> Yangqin Jiang, Yixiang Fang, Chenhao Ma, Xin Cao, and Chunshan Li. “Effective community search over large star-schema heterogeneous information networks”. Proceedings of the VLDB Endowment, 15(7): xxx–xxx, 2022.

## Introduction

In this paper, we study the problem of Community Search over Star-schema HINs (or CSSH problem in short), which aims to search the most—likely community containing a set of query vertices Q from a star-schema HIN, without specifying the parameters like meta-paths and relational constraints. To model the community that can well capture the rich semantic relationships carried by query vertices Q, we find the set of vertices under the meta-path-based core model, by maximizing the set of shared meta-paths satisfying the property of non-nestedness. We first develop efficient online query algorithms. We further boost the query efficiency by designing a novel compact index structure and an index-based query algorithm. Our experimental results on four real large star-schema HINs show that the proposed solutions are effective and efficient for searching communities over large HINs.

## Environment

The codes of CSSH are implemented and tested under the following development environment:

- Hardware : Intel(R) Xeon(R) Gold 6226R 2.90GHz CPU and 256GB of memory.
- Operation System : Ubuntu 20.04.4 LTS (GNU/Linux 5.13.0-40-generic x86_64)
- Java Version : jdk 11.0.15

## Datasets

We use four real star-schema HINs: PubMed, IMDB, DBLP and Foursquare. The data.zip file in the repository is the corresponding datasets. The detailed statistics and original sources of these datasets are in our paper.

## Acknowledgements

Chunshan Li was supported by the National Key Research and Development Program of China (No.2018YFB1700400), NSFC under Grant 61902090, and the Major Scientific and Technological
Innovation Project of Shandong Province of China (2021ZLGX05, 2020CXGC010705). Yixiang Fang was supported by NSFC under Grant 62102341, and Basic and Applied Basic Research Fund in Guangdong Province under Grant 2022A1515010166. Xin Cao was supported by ARC DE190100663.