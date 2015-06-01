## Using Paging With MaJorToM ##

Since version 1.1.0 the MaJorToM engine supports a paging mechanism which can be used by applications instead of the "unpaged" methods of MaJorToM API. The paged methods can be used by a set of special index classes or a set of paged construct classes.

### Using Paged Indexes ###

As an extension of the base indexes, the new MaJorToM version includes a set of special indexes supporting a paging mechanism. Each method exists in two versions expecting two or three arguments. The optional third argument has to be an instance of java.util.Comparator which should be used to sort the returned list. If the comparator is missing, the list will be sort by default rules.

  * IPagedScopeIndex
  * PagedTypeInstanceIndex
  * IPagedTransitiveTypeInstanceIndex
  * IPagedSupertypeSubtypeIndex
  * IPagedLiteralIndex
  * IPagedIdentityIndex
  * IPagedConstructIndex

```
IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
```


### Using Paged Constructs ###

As an alternative for the paged indexes is the usage of paged constructs.

  * IPagedTopic
    * getTypes
    * getSupertypes
    * getAssociationsPlayed
    * getRolesPlayed
    * getNames
    * getOccurrences
  * IPagedName
    * getVariants
  * IPagedAssociation
    * getRoles

```
Topic topic = topicMap.createTopic();
IPagedTopic pagedTopic = (IPagedTopic) topic;
/*
 * get number of names
 */
int number = pagedTopic.getNumberOfNames();
```