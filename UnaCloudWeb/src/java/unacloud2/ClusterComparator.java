package unacloud2;
import java.util.Comparator;

public class ClusterComparator implements Comparator<Cluster>{

	@Override
	public int compare(Cluster o1, Cluster o2) {
		return (o1.getName()).compareTo(o2.getName());
	}

}
