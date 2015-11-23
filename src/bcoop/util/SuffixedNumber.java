package bcoop.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SuffixedNumber implements Comparable<SuffixedNumber> {
	private static final long serialVersionUID = -3650538390902668138L;
	private long value;
	private String type;

	
	public SuffixedNumber(long value, String type){
		this.value = value;
		this.type = type;
	}
	
	public static long fromString(String inputString){
		Pattern suffixedPattern = Pattern.compile("^([0-9.,-]+) *([A-Za-z ]*)$");
		Matcher match = suffixedPattern.matcher(inputString.trim());
		if(!match.matches()){
			throw new NumberFormatException("'"+inputString+"' is not a valid format");
		}
		double number = Double.parseDouble(match.group(1));
		String suffixString = match.group(2).toUpperCase();
		Suffix s = Suffix.findSuffix(suffixString);
		return (long) (number * Math.pow(10, s.exponent));
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		Suffix suffix = Suffix.getBestSuffix(this.value);
		buffer.append(suffix.toString(this.value));
		buffer.append(this.type);
		return buffer.toString();
	}

	public int compareTo(SuffixedNumber other) {
		if(this.value < other.value){
			return -1;
		}
		else if(this.value > other.value){
			return 1;
		}
		return 0;
	}
}
