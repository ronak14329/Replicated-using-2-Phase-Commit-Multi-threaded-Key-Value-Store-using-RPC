# Replicated-using-2-Phase-Commit-Multi-threaded-Key-Value-Store-using-RPC
For this project, you will extend Project #2 in two distinct ways.
1) Replicate your Key-Value Store Server across 5 distinct servers. In project #2, you used a single
instance of a Key-Value Store server. Now, to increase Server bandwidth and ensure availability, you
need to replicate your key-value store at each of 5 different instances of your servers. Note that your
client code should not have to change radically, only in that your clients should be able to contact any
of the five KV replica servers instead of a single server and get consistent data back from any of the
replicas (in the case of GETs). You client should also be able to issue PUT operations and DELETE
operations to any of the five replicas.
2) On PUT or DELETE operations you need to ensure each of the replicated KV stores at each replica is
consistent. To do this, you need to implement a two-phase protocol for updates. We will assume no
servers will fail such that 2 Phase Commit will not stall, although you may want to defensively code
your 2PC protocol with timeouts to be sure. Consequently, whenever a client issues a PUT or a
DELETE to *any* server replica, that receiving replica will ensure the updates have been received (via
ACKs) and commited (via Go messages with accompanying ACKs).
As in project #1, you should use your client to pre-populate the Key-Value store with data and a set of keys. The
composition of the data is up to you in terms of what you want to store there. Once the key-value store is
populated, your client must do at least five of each operation: 5 PUTs, 5 GETs, 5 DELETEs
