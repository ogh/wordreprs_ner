# Word Representations for Named Entity Recognition #

This repository contains [the code][ratinov] code introduced by
Ratinov et al. (2009) and subsequently used by Turian et al. (2010) and
Stenetorp et al. (2012) with some minor patches applied and instructions for
replicating the results. It also benefits from being slightly slimmer than the
original distribution by stripping out duplicates of the same code and
resources.

[ratinov]: http://cogcomp.cs.illinois.edu/Data/ACL2010_NER_Experiments.php

## Word Representations ##

The word representations used by Turian et al. (2010) are available from
[here][turian].

The word representations used by Stenetorp et al. (2012) are available [here][data-ner] in a format that is suitable for the NER task.

[turian]: http://metaoptimize.com/projects/wordreprs/

## Replicating the Experiments ##

With the data and code provided on this page you can replicate the experiments conducted for Stenetorp et al. (2012).
In case you have problems please let us know.

### Setup ###

After cloning this repository you will have to download the wordrepresentations and 
the corpora that were used for evaluation and unpack them to their respective folders.

Please download and unpack the archive containing formatted versions of the AnEM, Disease and GeneTag corpora from [here][corpora-ner].
Organize them in the following way in your local data folder.

    data
    ├── ...
    ├── GoldData
    │   ├── AnEM
    │   │   ├── Dev
    │   │   ├── Test 
    │   │   └── Train
    │   ├── Disease
    │   │   ├── Dev
    │   │   ├── Test
    │   │   └── Train
    │   └── GeneTag
    │       ├── Dev
    │       ├── Test 
    │       └── Train
    └── ...

[corpora-ner]: http://anonoia.org/webdav/data/stenetorp2012size_ner/stenetorp2012size_ner_corpora.tar.gz
    
Next, download the NER-Experiments folder provided by Turian et al. (2010) from 
[here][turian-ner] and copy the folders "data/WordEmbedding" and "data/BrownHierarchicalWordClusters" to your data folder.  
Move the current content of your "data/BrownHierarchicalWordClusters" folder
to a new subfolder called "data/BrownHierarchicalWordClusters/outDomain".

Finally download the representations introduced by Stenetorp et al. (2012) from [here][data-ner]
and unpack the Brown-clusters to "data/BrownHierarchicalWordClusters/inDomain" and the distributed word representations to 
"data/WordEmbedding".

[data-ner]: http://anonoia.org/webdav/data/stenetorp2012size_ner/stenetorp2012size_ner_data.tar.gz
[turian-ner]: http://cogcomp.cs.illinois.edu/Data/ACL2010_NER_Experiments.php


In the end you should have the following structure in your data folder.

    data
    ├── BrownHierarchicalWordClusters
    │   ├── inDomain
    │   │   ├── c1000.txt
    │   │   ├── c100.txt
    │   │   ├── c150.txt
    │   │   ├── c320.txt
    │   │   ├── c500.txt
    │   └── outDomain
    │       ├── brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1.lowerCase.txt
    │       ├── brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1.txt
    │       ├── brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1.upperCase.txt
    │       ├── brown-rcv1.clean.tokenized-CoNLL03.txt-c100-freq1.txt
    │       ├── brown-rcv1.clean.tokenized-CoNLL03.txt-c3200-freq1.txt
    │       └── brown-rcv1.clean.tokenized-CoNLL03.txt-c320-freq1.txt
    ├── GoldData
    │   └── ...
    ├── Models
    └── WordEmbedding
        ├── david.txt
        ├── google-phrasal-clusers.txt
        ├── hlbl_reps_clean_1.rcv1.clean.tokenized-CoNLL03.case-intact.txt
        ├── hlbl_reps_clean_2.50d.rcv1.clean.tokenized-CoNLL03.case-intact.txt
        ├── model-1750000000.LEARNING_RATE=1e-09.EMBEDDING_LEARNING_RATE=1e-06.EMBEDDING_SIZE=200.txt
        ├── model-2030000000.LEARNING_RATE=1e-09.EMBEDDING_LEARNING_RATE=1e-06.EMBEDDING_SIZE=100.txt
        ├── model-2280000000.LEARNING_RATE=1e-08.EMBEDDING_LEARNING_RATE=1e-07.EMBEDDING_SIZE=25.txt
        └── model-2280000000.LEARNING_RATE=1e-08.EMBEDDING_LEARNING_RATE=1e-07.EMBEDDING_SIZE=50.txt
        
        
### Running an experiment ###

Each experiment has one corresponding config file that specifies its parameters.
The config files are located in the "config" directory and named according to the following scheme:
    
    {CorpusName}-[(bio)|(news)]+-domain-{wordreptype-info}+.config
    
Therefore, the names start with a corpus-identifier followed by one or multiple occurrences of 
"bio" or "news" followed by "-domain-" and one or multiple occurrences of wordrepresentation identifiers.
Each "bio"/"news" corresponds to one wordreptype-info and indicates whether a wordrepresnentation was induced on 
bio or news data. The first "bio"/"news" corresponds to the first wordreptype-info, the second to the second
wordreptype-info and so on.

    Corpus identifiers:
        AnEM = AnEM
        Disease = NCBID
        GeneTag = BC2GM
        
Example:

    GeneTag-bio-news-domain-clarkne-hlbl.config

This means: The experiment will be conducted on the GeneTag(BC2GM) corpus and ClarkNE representations 
induced on bio-data will be used in combination with HLBL representations induced on newswire data.

All parameters available in the config files are identical to the ones from Turian et al. (2010) with two exceptions.
The newly introduced parameter "pathToTrainDevTest" specifies the directory where your corpus Train/Dev/Test split is avaiable.
The "isUppercaseWordEmbeddings" parameter specifies for each used wordrepresentation files whether it is uppercased only.

Before you can run your first experiment, you will have to run 

    ./cleanCompile
    
from within your smbm-ner-experiments folder.

Now you can start experiments by using modified versions of the example command below.
Note that the command below must be executed from within your smbm-ner-experiments folder.

Example program call:

    nohup nice java -Xmx4000m -classpath LBJ2.jar:LBJ2Library.jar:bin:stanford-ner.jar:stanford-ner.src.jar:lucene-core-2.4.1.jar \
                    ExperimentsSMBM/PerformExperimentGivenConfig  \
                    ../config/AnEM-bio-domain-brown-c1000.config \
                    > smbm.contra.result.AnEM-bio-domain-brown-c1000.txt  &


You may need to allow for more than 4000m of memory especially for the Google-Phrase-Cluster runs.


## Citing ##

If you use the code without any word representations please cite:

    @InProceedings{ratinov2009design,
      author    = {Ratinov, Lev  and  Roth, Dan},
      title     = {Design Challenges and Misconceptions
          in Named Entity Recognition},
      booktitle = {Proceedings of the Thirteenth Conference
          on Computational Natural Language Learning (CoNLL-2009)},
      month     = {June},
      year      = {2009},
      address   = {Boulder, Colorado},
      publisher = {Association for Computational Linguistics},
      pages     = {147--155},
    }

If you use the code and the word representations in combination with the
Turian et al. (2010) embeddings please cite:

    @InProceedings{turian2010word,
      author    = {Turian, Joseph  and  Ratinov, Lev-Arie
          and  Bengio, Yoshua},
      title     = {Word Representations: A Simple and General Method
          for Semi-Supervised Learning},
      booktitle = {Proceedings of the 48th Annual Meeting of the Association
          for Computational Linguistics},
      month     = {July},
      year      = {2010},
      address   = {Uppsala, Sweden},
      publisher = {Association for Computational Linguistics},
      pages     = {384--394},
    }

If you use the code and the word representations in combination with the
Stenetorp et al. (2012) embeddings please cite:

    @inproceedings{stenetorp2012size,
        author      = {Stenetorp, Pontus and Soyer, Hubert and Pyysalo, Sampo
            and Ananiadou, Sophia and Chikayama, Takashi},
        title       = {Size (and Domain) Matters: Evaluating Semantic Word
            Space Representations for Biomedical Text},
        year        = {2012},
        booktitle   = {Proceedings of the 5th International Symposium on
            Semantic Mining in Biomedicine},
        note        = {to appear},
    }
