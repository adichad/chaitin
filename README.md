Chaitin: search engine from scratch
===================================

**INSTRUCTIONS**

Prerequisites
* Linux / Mac OS X
* 2 GB RAM allocatable to java
* internet connection (to download scala-library if not present in gradle cache)
* Java 8 JDK
  * ``java -version`` should work
  * ``javac -version`` should work
* Included External Dependencies
  * **gradle** build tool
  * **scala**-library:2.7.11
  * **specs2** test framework

Build & Run

```
$ tar -xzvf chaitin.tgz
$ cd chaitin
$ ./gradlew dist
$ cd build/distributions
$ tar -xzvf chaitin-0.1.0.tgz
$ cd chaitin-0.1.0
$ ./chaitin.sh <path/to/simplewiki.tsv>
```

Sample Output

```
[INFO ] pid file: /tmp/chaitin.pid
indexing: simplewiki.tsv
....................................................................................................
indexed [100990] docs in [44138] msecs
search> black hole sun
102357	Soundgarden
258424	Black Hole Sun
131337	List of Soundgarden awards
131726	Fell on Black Days
3017	Star
3506	Black hole
131375	Songs from the Superunknown
102485	Superunknown
131328	Soundgarden discography
4570	A Brief History of Time
ranked [26] docs in [126] msecs

search> ^C
```

**RUNTIME PERFORMANCE**

* indexing
  * text tokenization / analysis
    * regex tokenizer
      * O(k) space and time in the size of the analyzed text
    * lowercase token filter
      * O(k) space and time
      * can be done in-place
  * creation of in-memory inverted / forward indexes
    * O(N * k * log(k)) time complexity, O(N * k) space complexity (k: dictionary terms in the corpus; N: documents in the corpus)
  * practical: 100990 documents indexed in 40 seconds

* search
  * text tokenization / analysis / Query formation
    * same as during indexing for a field
  * query-document matching
    * O(N * log(k)) time complexity (N: documents in corpus; k: terms in query)
    * O(N) space complexity, can be reduced to O(1)
  * scoring
    * O(N * j^2) time complexity (all pairs positional distance)
  * practical: median query time ~ 40 milliseconds


**RANKING STRATEGY**

* Matching: All word match (Conjunction)
  * no fuzzy matching
  * no sophisticated analysis/normalization
  * no criteria relaxation (quorum, etc.)

* Ranking: TF-IDF + proximity (phrase permutation matches are best, graceful degradation)
  * O(N^2) Algorithm
  * No query time boosts for Multi-field match ranking
  * no differentiation between exact phrase and out-of-order phrase matches

**POSSIBLE IMPROVEMENTS**

In order of priority:
* unit tests (completely lacking)
* extraneous use of memory for indexing, search. little use of primitive arrays/compression encodings
* too much copying of data, boxing/unboxing, may lead to GC pressure
* No thread safety between indexing and search, no multi-threading
* on-disk index representation for faster reloading
* more analyzers
* more queries (disjunction/quorum, fuzzy, span)
* better dictionary structure for prefix/fuzzy matching
* document deletion
* segmented indexes, segment merging
* no skip-lists while traversing posting lists
* code comments
* IOC / Configuration / Dependency Injection


**CREDITS**

* scala programming language
* my memory of lucene 2.x/3.x code