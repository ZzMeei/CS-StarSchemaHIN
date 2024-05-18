# CS-StarSchemaHIN

This repository contains Java codes and datasets for the paper:

> Yangqin Jiang, Yixiang Fang, Chenhao Ma, Xin Cao, and Chunshan Li. “Effective community search over large star-schema heterogeneous information networks”. Proceedings of the VLDB Endowment, 15(11): 2307 - 2320, 2022. 
>
> doi:10.14778/3551793.3551795

### Update 2024-05-19

Since there may be some issues about the data.zip file on the github, we also share the data file on google drive: https://drive.google.com/drive/folders/1cCghFJOw5WTHXizzw7wc7giSGg2BxN4S?usp=share_link 

## Introduction

In this paper, we study the problem of Community Search over Star-schema HINs (or CSSH problem in short), which aims to search the most—likely community containing a set of query vertices Q from a star-schema HIN, without specifying the parameters like meta-paths and relational constraints. To model the community that can well capture the rich semantic relationships carried by query vertices Q, we find the set of vertices under the meta-path-based core model, by maximizing the set of shared meta-paths satisfying the property of non-nestedness. We first develop efficient online query algorithms. We further boost the query efficiency by designing a novel compact index structure and an index-based query algorithm. Our experimental results on four real large star-schema HINs show that the proposed solutions are effective and efficient for searching communities over large HINs.

## Environment

The codes of CSSH are implemented and tested under the following development environment:

- Hardware : Intel(R) Xeon(R) Gold 6226R 2.90GHz CPU and 256GB of memory.
- Operation System : Ubuntu 20.04.4 LTS (GNU/Linux 5.13.0-40-generic x86_64)
- Java Version : jdk 11.0.15

## Datasets

We use four real star-schema HINs: PubMed, IMDB, DBLP and Foursquare. The data.zip file in the repository is the corresponding datasets. The detailed statistics and original sources of these datasets are in our paper.

## How to Run the Codes

### A. Code Complilation

The CSSH file folder is an IDEA (IntelliJ IDEA) project file and can be opened and run through IDEA as a Java project (recommended). And it is also right to use jdk tools to complilate the codes in CSSH/src directly.

You can complilate corresponding java main classes in the CSSH/src/test by : 

`javac -d . *.java`

and run class files by :

`java *`

where * means java file name (as well as class name)

### B. Data Generation

The data.zip file in the repository is the corresponding datasets. The Config.java and DataReader.java in CSSH/src/util implementate the function of reading data (edges, vertices and schemas for graphs). 

To read datasets sucessfully, you need to motify the corresponding path string in Config.java.

The original sources of these datasets are stated in our paper.

### C. Experimentation

The file path mentioned in the folloing is started with "CSSH/src/test"

#### Effectiveness evaluation

- Community compactness : DiameterTest.java
- Similarity of community members : PathSimTest.java
- Semantic richness : SizeTest.java
- Relationships closeness : SizeTest.java
- A case study : CaseTest.java

#### Efficiency evaluation

- Online and index-based query algorithms : KTest.java and KTestSlow.java
- Scalability test : ScalableTest.java
- Index space cost analysis : IndexTest.java
- Index construction time analysis : ScalableTest.java

#### **Detailed Reproducibility Script**

Before the reproducibility, you should download the dataset from the repo and modify file "CSSH/src/util/Config.java" based on the dataset path on your machine.

For section 5.2 Effectiveness Evaluation, you need to run DiameterTest.java to reproduce the result of community compactness; run PathSimTest.java to reproduce the result of similarity of community members; run SizeTest.java to reproduce the result of semantic richness and the result of relationships closeness.

To generate queries for the experiments of Effectiveness Evaluation, you need to run function geneQueryiesSize() in SizeTest.java and you will get four query files with query size equal to 2, 3, 4, 5.

For section 5.3 Efficiency Evaluation, you need to run KTest.java and KTestSlow.java get the efficiency results of two online query algorithms and one indes-based qeury algorithms; run ScalableTest.java to get the result of scalability for two online query algorithms; run IndexTest.java to get the result of index space cost analysis; run ScalableTest.java to get the result of index construction time analysis.

To generate queries for the experiments of Efficiency Evaluation, you need to run function geneQueriesK() in KTest.java and you will get one query files.

Note that for every file in "CSSH/src/test", we have some notes for each function in the file which describes the meaning of this function in brief.

## Drawing

drawing_code includes all python scripts which are used to draw all experiment reslut pictures in the paper.

To run python codes in drawing_code, you need the enviroment with following packages :

- python
- networkx
- matplotlib
- numpy

## Acknowledgements

Chunshan Li was supported by the National Key Research and Development Program of China (No.2018YFB1700400), NSFC under Grant 61902090, and the Major Scientific and Technological
Innovation Project of Shandong Province of China (2021ZLGX05, 2020CXGC010705). Yixiang Fang was supported in part by NSFC under Grant 62102341, Basic and Applied Basic Research Fund in
Guangdong Province under Grant 2022A1515010166, and Shenzhen Science and Technology Program ZDSYS20211021111415025. Xin Cao was supported by ARC DE190100663.