package VO;

import java.util.ArrayList;
import java.util.List;

/**
 * Deployment Request VO
 *  @author s.guzmanm
 */
public class DeploymentRequest {
    //Class for storing the cluster
    class Cluster
    {

        class Node
        {
            //Id
            private int id;
            //Given hardware profile
            private int hwp;
            //Given quantity
            private int quantity;
            //Host name
            private String gHostName;
            //Whether it is of high availability or not
            private boolean type;

            public Node(int id, int hwp, int quantity, String gHostName, boolean type) {
                this.id = id;
                this.hwp = hwp;
                this.quantity = quantity;
                this.gHostName = gHostName;
                this.type = type;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getHwp() {
                return hwp;
            }

            public void setHwp(int hwp) {
                this.hwp = hwp;
            }

            public int getQuantity() {
                return quantity;
            }

            public void setQuantity(int quantity) {
                this.quantity = quantity;
            }

            public String getgHostName() {
                return gHostName;
            }

            public void setgHostName(String gHostName) {
                this.gHostName = gHostName;
            }

            public boolean isType() {
                return type;
            }

            public void setType(boolean type) {
                this.type = type;
            }
        }
        //Id
        private int id;
        //Nodes of the cluster
        private List<Node> nodes;

        public Cluster(int id) {
            this.id = id;
            nodes=new ArrayList<>();
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<Node> getNodes() {
            return nodes;
        }

        public void setNodes(List<Node> nodes) {
            this.nodes = nodes;
        }

        public void addNode(int id, int hwp, int quantity, String gHostName, boolean type)
        {
            nodes.add(new Node(id,hwp,quantity,gHostName,type));
        }

        public void removeNode(Node n)
        {
            nodes.remove(n);
        }

    }


    //Time in hours
    private int time;
    //CLuster
    private Cluster cluster;

    public DeploymentRequest(int time, int idCluster) {
        this.time = time;
        this.cluster = new Cluster(idCluster);
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public void addNode(int id, int hwp, int quantity, String gHostName, boolean type)
    {
        cluster.addNode( id,  hwp,  quantity,  gHostName, type);
    }
}
