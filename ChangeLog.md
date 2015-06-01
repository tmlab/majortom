# Version 1.3.0 #

# Version 1.2.0 #

  * new interface IPagedTopicMap with following methods
    * **List:getAssociations(int, int)**
    * **List:getAssociations(int, int, Comparator)**
    * **long:getNumberOfAssociations()**
    * **List:getTopics(int, int)**
    * **List:getTopics(int, int, Comparator)**
    * **long:getNumberOfTopics()**
  * new methods at IPagedConstructIndex
    * **List:getAssociations(int, int)**
    * **List:getAssociations(int, int, Comparator)**
    * **long:getNumberOfAssociations()**
    * **List:getTopics(int, int)**
    * **List:getTopics(int, int, Comparator)**
    * **long:getNumberOfTopics()**
  * new **getNumberOf...** methods at paged indexes

# Version 1.1.2 #

  * Caching for MaJorToM (O)RDBMS Topic Map Store
  * cached indexes
  * MaJorToM (O)RDBMS Topic Map Store support for MySQL, H2
  * new Method _getBestLabel_ to get the best label for a topic item
  * new Method _getBestLabel_ with theme to get the best label for a topic item and prefer names with the given theme
  * new Method _getBestLabel_ with theme and strict to get the best label for a topic item and prefer names with the given theme or return nothing if strict is set and no scoped name exists
  * new Method @ ITopicMapStore
    * isCachingEnabled
    * enableCaching
  * new Method @Index **clear** to remove all cached entries
  * rename method @TopicMapStoreImpl **doModifyType(ITopic,ITopic)** to  **doModifyTopicType(ITopic,ITopic)**
  * modify construct of WGS84Degree(String) ::= remove Orientation param
> > -> WGS84Coordinate has to call declareAsLongitude/Latitude

# Version 1.1.1 #

  * MaJorToM (O)RDBMS Topic Map Store
    * new topic map store implementation supporting PostGreSQL databases
    * persistence
    * transaction and revision support
  * [Documenation](http://docs.topicmapslab.de/majortom) for the usage of MaJorToM
  * storage of meta information for each revision as simple key-value pairs