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

The word representations used by Stenetorp et al. (2012) will be made
available shortly.

[turian]: http://metaoptimize.com/projects/wordreprs/

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
