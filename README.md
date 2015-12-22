## Project description

The scoring functions to assess spectrum similarity play a crucial role in many computational mass spectrometry algorithms. They can be used to compare an acquired MS/MS spectrum against two different types of target spectra: either against a theoretical MS/MS spectrum derived from a peptide from a sequence database, or against another, previously acquired acquired MS/MS spectrum. The former is typically encountered in database searching, while the latter is used in spectrum clustering, spectral library searching, or the detection of unique spectra between different data sets in comparative proteomics studies. 

The most commonly used scoring functions in experimental versus theoretical spectrum matching could be divided into two groups:
 -- non-probabilistic (cross correlations which was used for [SEQUEST](http://fields.scripps.edu/sequest/))
 -- probabilistic (cumulative binomial probability derived scoring functions in [Andromeda](http://141.61.102.17/maxquant_doku/doku.php?id=maxquant:andromeda) and [MS-Amanda](http://ms.imp.ac.at/?goto=msamanda))
 
Scoring functions for the comparison of two experimental spectra:
 -- Normalized dot product (most commonly used in spectrum library search algorithms such as [SpectraST](http://tools.proteomecenter.org/wiki/index.php?title=Software:SpectraST), [BiblioSpec](https://skyline.gs.washington.edu/labkey/project/home/software/BiblioSpec/begin.view)
 -- Pearson’s and Spearman's correlation coefficients

## Scoring functios
This project contains these enlisted scoring functions in Project description. The scoring functions to compare an acquired MS/MS spectrum against:

- a theoretical spectrum via
     -- SEQUEST-like scoring function (non-probabilistic)
     -- Andromeda-like scoring function (probabilistic)

- another acquired spectrum via
    -- Dot product
    -- Normalized dot product
    -- Pearson's r
    -- Spearman's rho
    -- Mean Squared Error (MSE)
    -- Probabilistic scoring functon (including peak intensities)
    

The scoring functions listed here were used to evaluate their ability to assess spectrum similarity on a book chapter on CPTAC data set. The settings are on the bookChapter.properties and the jar file can be downloaded [here](TODO:genesis).
