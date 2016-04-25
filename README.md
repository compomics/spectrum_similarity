# Project description

The scoring functions to assess spectrum similarity play a crucial role in many computational mass spectrometry algorithms. These scoring functions can be used to compare an acquired MS/MS spectrum against two different types of target spectra: either against a theoretical MS/MS spectrum derived from a peptide from a sequence database, or against another, previously acquired acquired MS/MS spectrum. The former is typically encountered in database searching, while the latter is used in spectrum clustering, spectral library searching, or the detection of unique spectra between different data sets in comparative proteomics studies. 

The most commonly used scoring functions in experimental versus theoretical spectrum matching could be divided into two groups:

- non-probabilistic (cross correlations which was used for [SEQUEST](http://fields.scripps.edu/sequest/))
- probabilistic (cumulative binomial probability derived scoring functions in [Andromeda](http://141.61.102.17/maxquant_doku/doku.php?id=maxquant:andromeda) and [MS Amanda](http://ms.imp.ac.at/?goto=msamanda))
 
 
Scoring functions for the comparison of two experimental spectra:
- Normalized dot product (most commonly used in spectrum library search algorithms such as [SpectraST](http://tools.proteomecenter.org/wiki/index.php?title=Software:SpectraST), [BiblioSpec](https://skyline.gs.washington.edu/labkey/project/home/software/BiblioSpec/begin.view))
- Pearson’s and Spearman's correlation coefficients

# Avaliable scoring functions
This project contains the enlisted scoring functions in [Project description](## Project description). The scoring functions in order to compare an acquired MS/MS spectrum against:

- a theoretical spectrum via
 - SEQUEST-like scoring function (non-probabilistic)
 - Andromeda-like scoring function (probabilistic)

- another acquired spectrum via
 - Dot product
 - Normalized dot product
 - Normalized dot product with introducting weights from peak intensities 
 - Pearson's r
 - Spearman's rho
 - Mean Squared Error (MSE) (and also root MSE)
 - Median Squared Error (MdSE) (and also root MdSE)
 - Probabilistic scoring functon (including peak intensities)

# Citation

[Yılmaz et al: J Proteome Res. Publication Date (Web): April 18, 2016 (DOI:10.1021/acs.jproteome.6b00140)](http://pubs.acs.org/doi/abs/10.1021/acs.jproteome.6b00140)

If you use our differential pipeline stand-alone tool or GUI version, please include the reference above. 


----

# Download

### Differential pipeline

The  probabilistic scoring function was succesfully applied on the differential pipeline in order to compare two experimental data sets in a differential analysis. 

A stand-alone program and the GUI version of this stand-alone program can be downloaded [here](http://genesis.ugent.be/maven2/com/compomics/scoring_pipeline/1.0/scoring_pipeline-1.0.zip). 


### A pairwise spectrum view GUI
A pairwise spectrum view GUI enables the manual inspection of how spectra actually look alike and can be downloaded <a href="http://genesis.ugent.be/maven2/com/compomics/spectrum_similarity_pairwise_GUI/0.1/spectrum_similarity_pairwise_GUI-0.1.zip" onclick="trackOutboundLink('usage','download','spectrum-similarity-gui','http://genesis.ugent.be/maven2/com/compomics/spectrum_similarity_pairwise_GUI/0.1/spectrum_similarity_pairwise_GUI-0.1.zip'); return false;">here</a>.

### Book chapter

The scoring functions enlisted [Avaliable scoring functions] were used to evaluate their ability to assess spectrum similarity by evaluating them on one of the CPTAC data sets. This has been described in a book chapter.

The program to compare spectra with the avaliable scoring functions, against either theoretical or experimental spectra can be downloaded [here](http://genesis.ugent.be/maven2/com/compomics/spectrum_similarity/0.1/spectrum_similarity-0.1.zip).

The settings to perform spectrum comparison are in the bookChapter.properties file. 

----

# Usage
See the [wiki](https://github.com/compomics/spectrum_similarity/wiki) for additional information on how to setup, run and configure spectrum comparison related projects.

