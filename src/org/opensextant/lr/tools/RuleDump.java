package org.opensextant.lr.tools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

// Simple program to pull the JAPE rules out of a file and dump to console as name, pattern, action  
public class RuleDump {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		File inDir = new File(args[0]);

		String rulePatternString = "Rule:(.+?)\\((.+?)-->(.+?)}";

		Pattern rulePattern = Pattern
				.compile(rulePatternString, Pattern.DOTALL);
		String[] exts = { "jape" };

		Collection<File> files = FileUtils.listFiles(inDir, exts, true);

		for (File f : files) {
			String rulesFileContents = "";
			try {
				rulesFileContents = FileUtils.readFileToString(f, "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Matcher ruleMatcher = rulePattern.matcher(rulesFileContents);

			while (ruleMatcher.find()) {
				MatchResult res = ruleMatcher.toMatchResult();
				int sName = res.start(1);
				int eName = res.end(1);
				int sBody = res.start(2);
				int eBody = res.end(2);
				int sAction = res.start(3);
				int eAction = res.end(3);

				String ruleName = rulesFileContents.substring(sName, eName);
				String ruleBody = rulesFileContents.substring(sBody, eBody);
				String ruleAction = rulesFileContents.substring(sAction,
						eAction);
				System.out.println(f.getParentFile().getName() + "\t"
						+ f.getName() + "\t" + clean(ruleName) + "\t"
						+ clean(ruleBody) + "\t" + clean(ruleAction));
			}

		}
	}

	public static String clean(String in) {
		String tmp = in.replaceAll("[\n\r]", " ").replaceAll("\\s+", " ")
				.trim();

		return tmp;

	}
}
