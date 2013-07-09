bridge-bounding
===============

Implementation of the Bridge Bounding algorithm for local community detection. 
Detailed description of the algorithm is available on: http://arxiv.org/abs/0902.0871
In case you make use of this code in your research, please cite the above paper.

The implementation uses the JUNG library to handle graphs: http://jung.sourceforge.net/
To get quickly started with the use of the algorithm, have a look at a very simple example (using synthetic community structure) in the bbound.SimpleSyntheticTest.java class (under the test folder).

This project contains also implementations for three other well-known local community detection methods, namely:
- Clauset, described in [Finding local community structure in networks](http://arxiv.org/abs/physics/0503036);
- Luo, Wang and Promislow, described in [Exploring Local Community Structures in Large Networks](http://dl.acm.org/citation.cfm?id=1249100);
- Bagrow, described in [Evaluating Local Community Methods in Networks]((http://iopscience.iop.org/1742-5468/2008/05/P05001)). 

For more information or support, contact: papadop@iti.gr or symeon.papadopoulos@gmail.com