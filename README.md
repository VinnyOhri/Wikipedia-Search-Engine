# Wikipedia-Search-Engine
WIKIPEDIA SEARCH ENGINE

Basically we have to design a search engine where we are given a query and we have to retrieve the most relevant documents in < 1 second irrespective of the system requirements (does not depend upon RAM or processor or any other system requirement) . You can assume you are given basic system requirements like RAM of size 512 MB and basic processor. Another major requirement will be to make our indexing size 1/4th of the original data size. 

Basic​ ​Stages​ ​(in​ ​order):
● XML parsing [Prefer SAX parser over DOM parser. If you use DOM parser, you can’t
scale it up for the full Wikipedia dump later on.]
● Tokenization
● Stop word removal
● Case folding
● Stemming
● Posting List / Inverted Index Creation
● Optimize


Desirable​ ​Features:
● Support for Field Queries. Fields include Title, Infobox, Body, Category, Links, and
References of a Wikipedia page. This helps when a user is interested in searching for
the movie ‘Up’ where he would like to see the page containing the word ‘Up’ in the title
and the word ‘Pixar’ in the Infobox. You can store field type along with the word when
you index.
● Index size should be less than 1⁄4 of dump size. [You can experiment with different index
compressing techniques.]
● Scalable index construction [See Chapter 4 in the ‘Intro to IR’ book.]


Evaluation​ ​Criteria:
Evaluation for phase I will be on below two criteria:
(a) Index creation time: less than 60 secs for Java, CPP and for python it’s less than 150
secs.
(b) Inverted index size: expected size is 25-30 mb.

We have a wikipedia dump of around 60 GB. In that dump we have xml document of wikipedia pages.  Firstly open any wikipedia document and try to see its structure. We have a single xml file which contains millions of pages or documents of wikipedia database. 





Its structure is as follows:
<page>
    <title>--------------------</title>
    <id>-----------------------</id>
    <text>--------It will contain four components-----
		COMPONENTS          REPRESENTATION IN XML PAGE
Infobox               	{{    }}
Categories         	[[ category :     ]]
References       	==references==
External Links    	==external links==
    </text>
</page>

Each document is differentiated by a page tag means starting of a document is reflected by a pag tag. Then there is title tag followed by id tag. A single wikipedia page has many id’s so we will give ourself a virtual id to differentiate between pages . One of the way is to give it sequential number as you parse the xml document.

PARSING
First step is to do parsing . It can be done by 2 parsers . DOM and SAX. We have used SAX parser. Now the question arises why ??
DOM
Tree model parser (Object based) (Tree of nodes).
DOM loads the file into the memory and then parse- the file.
Has memory constraints since it loads the whole XML file before parsing.
DOM is read and write (can insert or delete nodes).
If the XML content is small, then prefer DOM parser.
Backward and forward search is possible for searching the tags and evaluation of the information inside the tags. So this gives the ease of navigation.
Slower at run time.
SAX
Event based parser (Sequence of events).
SAX parses the file as it reads it, i.e. parses node by node.
No memory constraints as it does not store the XML content in the memory.
SAX is read only i.e. can’t insert or delete the node.
Use SAX parser when memory content is large.
SAX reads the XML file from top to bottom and backward navigation is not possible.
Faster at run time.


In SAX parser ,only one object is created when the parent tag is called that is <XML TAG>. Object buffers are cleared out before new page tag appears. In this way we use same object for whole xml file.

As our dump is of 60 GB we can’t bring whole dump into main memory(RAM) . So we cannot use DOM parser which will bring whole document in the main memory and will try to form tree structure of tags. Instead SAX parser is event driven . Here events are the tags. So instead of bringing whole dump into memory it will parse tag by tag. Hence our problem of 60GB dump dealing with main memory is solved.

 Why we are making an inverted index , why not normal index ?
In our query we will be given words and we have give back best 10 results related to that word. So document id should not be a key instead every word should be taken as key and see other properties. That’s why we have to form an inverted index. In case of normal index it will take O(n) time in inverted it will take O(1) time.
Normal index:  document id -> data
Inverted index:  data -> document id
A good example of inverted index is the index present at the end of book where we search with respect to words.

My index is in the form of default dictionary where each element is an list. This element will contain information related to that word in a particular document. Relating to every document their will be a list. Hence in index number of rows will be total vocabulary of wikipedia dump and number of columns will be number of documents. It will be very big( around 17 GB ). We can call it as Primary Index. 

Default Dictionary special property :
Usually, a Python dictionary throws a KeyError if you try to get an item with a key that is not currently in the dictionary. The defaultdict in contrast will simply create any items that you try to access (provided of course they do not exist yet). To create such a "default" item, it calls the function object that you pass in the constructor (more precisely, it's an arbitrary "callable" object, which includes function and type objects). For the first example, default items are created using int(), which will return the integer object 0. For the second example, default items are created using list(), which returns a new empty list object.
E.g. ) somedict = {} 
          print(somedict[3])                                 # KeyError 
         
         somedict = defaultdict(int) 
         print(someddict[3])                                # print int(), thus 0

In SAX parser nodes are the tags. We parse tag by TAG. Here tags are events. It follows stack like data structure. File handler will be called again and again. It has 3 elements - startElement, characters and endElement.  We have overwrite these 3 functions. 

We are using ‘utf-8’ encoding for title. 
UTF-8: can represent all characters
1 byte: Standard ASCII
2 bytes: Arabic, Hebrew, most European scripts (most notably excluding Georgian)
3 bytes: BMP
4 bytes: All Unicode characters
We have buffer for each title tag ,text tag ,id tag. 

TITLE PROCESSING


Convert everything to Lowercase.
Now we have to Tokenize the title. We do it using regex “\d+ | [\w]+”. It gives list of tokenized words. Like “rajat.jain-0807@gmail.com” , we get ['rajat', 'jain', '0807', 'gmail', 'com']. Its takes only alphanumeric data. space,’.’,’-’,’@’ are all removed.
Then we encode every word to ‘utf-8’ encoding.
Then we remove the stop words. Stop words are ‘is’,’and’,’the’ etc which do not add any importance in our information and their removal also reduces our size of data. StopWords are already present in a file provided by standford. We put these stop words in default dictionary and make every element(key) value as 1 so that when we have to search this default dictionary it returns 0 when that query word is not in stop list while removing stop words.In case of simple dictionary if word is not found it will return KeyError instead of zero.
Now we do the stemming. Stemming is finding the root word like care for cares,caring etc.If we will not do this then care , cares, caring will be treated as different words which will not make any sense. This reduces our size of data to handle. We have used famous Porter Stemmer.


RANKING


First, we did the ranking on the basis of term frequency giving different weightage to different fields.It has two drawbacks: first was denominator term which contains the total number of words in a particular document, due to this term even when all the words were unique different documents had different term frequencies  Example:doc1 contains Rajat Jain and doc2 contains Rajat jain in India .Now if query comes with Rajat  both the documents should be equally ranked but due to TF ,doc1 will be ranked higher .This is a wrong inference.So we remove the denominator term in term frequency.
Second Drawback is related to when there are more than one word in a query.In those cases we need to give high  weightage to word which is less frequent in corpus.This is handled by IDF. Example - if D1<s=5,total=10>, D2<s=5,t=5,total=10>, D3<s=1,t=1,d=10> understand these three in terms of tf alone and tf-idf. When there are multiple words in query ,then for a particular document you need to calculate tf-idf for each word in the query and then add them .After doing this for each document we rank them(explain the bad brute force approach -sunny, first find documents with all words then all words-1,then all word-2). Tf-idf of different words have to be added for a document not multi-plied otherwise it will become zero and many documents will become zero hence no ranking will be done in those cases. So add them. 



We have 3 functions title processing , text processing, creating index. They are in end tag. After doing title processing and text processing  we call creating index. 

