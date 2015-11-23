package bcoop.util;

import java.util.Locale;

class Suffix{
	int exponent;
	String longString;
	String shortString;

	final static Suffix[] allTheSuffix = new Suffix[]{
			new Suffix(0, "", ""),
			new Suffix(3, "K", "Kilo"),
			new Suffix(6, "M", "Mega"),
			new Suffix(9, "G", "Giga"),
			new Suffix(12, "T", "Tera"),
			new Suffix(15, "P", "Peta"),
			new Suffix(18, "E", "Exa"),
			new Suffix(21, "Z", "Zeta"),
			new Suffix(24, "Y", "Yotta")
	};
	
	public Suffix(int exponent, String shortString, String longString) {
		this.exponent = exponent;
		this.shortString = shortString;
		this.longString = longString;
	}

	public String toString(float value) {
		Double fractionValue = value/Math.pow(10, this.exponent);
		String fractionStr = String.format(Locale.US, "%.2f", fractionValue);
		if(fractionStr.endsWith("00")){
			fractionStr=String.format("%.0f", fractionValue);
		}
		return String.format("%s %s", fractionStr, this.longString);
	}

	public boolean matches(String suffixString) {
		if(suffixString.equalsIgnoreCase(shortString)) return true;

		int exponentTypeLength = Math.min(longString.length(), suffixString.length());
		String exponentType = suffixString.substring(0, exponentTypeLength);
		return exponentType.equalsIgnoreCase(longString);
	}

	public static Suffix findSuffix(String suffixString) {
		for(int i=allTheSuffix.length-1; i>=0; i--){
			Suffix s = allTheSuffix[i];
			if(s.matches(suffixString)){
				return s;
			}
		}
		throw new NumberFormatException("Could not parse the value '"+suffixString+"'");
	}
	
	public static Suffix getBestSuffix(long value) {
		double logValue = Math.log10(value);
		Suffix bestSuffix = allTheSuffix[0];
		for(Suffix currentSuffix : allTheSuffix){
			if(logValue >= currentSuffix.exponent){
				bestSuffix = currentSuffix;
			}
			else{
				break;
			}
		}
		return bestSuffix;
	}

}
