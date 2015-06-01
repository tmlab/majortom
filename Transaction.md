## Using Transaction ##

MaJorToM in-memory store provides an embedded transaction mechanism which can be used to modify any constructs of the topic map without direct effects to the underlying topic map.

### Creating a Transaction ###

To open a new transaction context the additonal ITopicMap interface of the MaJorToM API has to be used like the following once. Therefor the TopicMap interface of TMAPI has to be cast to ITopicMap.

```
 ITransaction transaction = topicMap.createTransaction();
```

The transaction context behaves like a topic map, which enables a normaly usage.

### Transaction Context ###

Each construct which should be used in the transaction has to be in the transaction context, which means, that the internal "topic map" has to be the transaction itselfs. Please note, that construct outside the transaction context modify the topic map directly.

To move a construct created outside the transaction context inside, the method **moveToTransactionContext** can be used.
```
/*
 * outside the context
 */
ITopic type = topicMap.createTopic();
/*
 * inside the context
 */
Topic type_ = transaction.moveToTransactionContext(type);
```

**Hint:** Each construct created within the transaction context are already inside the context!

### Commit and Roll-back ###

To commit all changes of the transaction the method **commit** has to be used.

```
transaction.commit();
```

To remove all changes of the transaction the method **rollback** can be used.

```
transaction.rollback();
```

**Hint:** Please note, that the transaction context is invalid after the successful execution of commit or rollback.