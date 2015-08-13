package unacloud2;

import java.util.Comparator;

public class DeployedClusterComparator implements Comparator<DeployedCluster>{

	@Override
	public int compare(DeployedCluster o1, DeployedCluster o2) {
		return (o1.getCluster().getName()).compareTo(o2.getCluster().getName());
	}
}
