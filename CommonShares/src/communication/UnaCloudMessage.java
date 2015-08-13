package communication;

import static com.losandes.utils.Constants.MESSAGE_SEPARATOR_TOKEN;

import java.util.Arrays;
/**
 * Not used
 * TODO do something with this class
 * @author G
 *
 */
public class UnaCloudMessage {
	String[] tokens;
	public int length;
	public UnaCloudMessage(String[] tokens) {
		this.tokens = tokens;
		length=tokens==null?0:tokens.length;
	}
	public UnaCloudMessage(String tokenString) {
		this.tokens = tokenString.split(MESSAGE_SEPARATOR_TOKEN);
		length=tokens==null?0:tokens.length;
	}
	public int getInteger(int pos){
		if(pos<tokens.length&&tokens[pos]!=null&&tokens[pos].matches("-?[0-9]+"))return Integer.parseInt(tokens[pos]);
		return -1;
	}
	public long getLong(int pos){
		if(pos<tokens.length&&tokens[pos]!=null&&tokens[pos].matches("-?[0-9]+"))return Long.parseLong(tokens[pos]);
		return -1;
	}
	public String getString(int pos){
		if(pos<tokens.length&&tokens[pos]!=null)return tokens[pos];
		return null;
	}
	public String[] getStrings(int pos,int length){
		return Arrays.copyOfRange(tokens,pos,length);
	}
	@Override
	public String toString() {
		return Arrays.toString(tokens);
	}
}
