package org.hbase.async;

public class HBaseClientFactory {

  public static HBaseClient getHBaseClient(final String quorum_spec) {
    HBaseClient client = new HBaseClient(quorum_spec);
//    client.has_root =false;
    return client;
  }
}
