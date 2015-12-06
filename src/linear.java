import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class linear {
	public linear(String A, String B) {
		init(A, B);
	}

	static String A = "", B = "";
	static char a_arr[], b_arr[], rev_a[], rev_b[];
	static double g = 5, h = 2;
	static double cc[], dd[], rr[], ss[];
	static LinkedList<Integer> s;
	static double match = -2, mismatch = 3;
	static int n = 50;
	static SmithWaterman sw = new SmithWaterman();

	public static void main(String args[]) {
		Options opt = new Options();
		opt.addOption("a", true, "Path to first sequence file");
		opt.addOption("b", true, "Path to second sequence file");
		opt.addOption("h", false, "Print this help message");
		opt.addOption("g", true, "Gap open penalty");
		opt.addOption("e", true, "Gap extension penalty");
		opt.addOption("m", true, "Match score");
		opt.addOption("i", true, "Mismatch score");
		opt.addOption("n", true, "Max characters per line in display");
		DefaultParser parser = new DefaultParser();
		CommandLine cl = null;
		try {
			cl = parser.parse(opt, args);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String usage = "java -jar linear.jar -a <path/to/first/sequence> -b <path/to/second/sequence> [parameters]";
		if (cl.hasOption("h")) {
			HelpFormatter f = new HelpFormatter();
			f.printHelp(usage, opt);
		}
		if (!cl.hasOption("a") || !cl.hasOption("b")) {
			System.out.println(usage);
		} else {
			Scanner fin = null;
			try {
				fin = new Scanner(new File(cl.getOptionValue("a")));
			} catch (FileNotFoundException e) {
				System.out.println("Invalid path to first sequence file");
				e.printStackTrace();
			}
			while (fin.hasNext()) {
				A += fin.next();
			}
			if (A.length() == 0) {
				System.out.println("Your first sequence is empty.");
				return;
			}
			try {
				fin = new Scanner(new File(cl.getOptionValue("b")));
			} catch (FileNotFoundException e) {
				System.out.println("Invalid path to second sequence file");
				e.printStackTrace();
			}
			while (fin.hasNext()) {
				B += fin.next();
			}
			if (B.length() == 0) {
				System.out.println("Your second sequence is empty.");
				return;
			}
			if (cl.hasOption("g")) {
				try {
					sw.alpha = Double.parseDouble(cl.getOptionValue("g"));
					g = Double.parseDouble(cl.getOptionValue("g"));
				} catch (NumberFormatException e) {
					System.out.println("Gap open penalty must be a number");
				}
			}
			if (cl.hasOption("e")) {
				try {
					sw.beta = Double.parseDouble(cl.getOptionValue("e"));
					h = Double.parseDouble(cl.getOptionValue("e"));
				} catch (NumberFormatException e) {
					System.out.println("Gap extension penalty must be a number");
				}
			}
			if (cl.hasOption("m")) {
				try {
					sw.match = Double.parseDouble(cl.getOptionValue("m"));
					match = -1*Double.parseDouble(cl.getOptionValue("m"));
				} catch (NumberFormatException e) {
					System.out.println("Match score must be a number");
				}
			}
			if (cl.hasOption("i")) {
				try {
					sw.mismatch = Double.parseDouble(cl.getOptionValue("i"));
					mismatch = -1*Double.parseDouble(cl.getOptionValue("i"));
				} catch (NumberFormatException e) {
					System.out.println("Mismatch score must be a number");
				}
			}
			if (cl.hasOption("n")) {
				try {
					linear.n = Integer.parseInt(cl.getOptionValue("n"));
				} catch (NumberFormatException e) {
					System.out.println("Max characters per line must be an integer");
				}
			}
			/*
			 * Local Alignment
			 */
			new linear(linear.A, linear.B);
			int a_len = a_arr.length, b_len = b_arr.length;
			/*
			 * Find start and end indices of the leftmost local alignment with a
			 * linear-space, score-only version of Smith-Waterman.
			 */
			Point end_indices = sw.findEndIndices(A, B, false, new Point());
			// System.out.println("end_indices=" + end_indices);
			Point start_indices = sw.findEndIndices(new String(rev_a), new String(rev_b), true, end_indices);
			// System.out.println("start_indices=" + start_indices);
			start_indices.x = a_len - start_indices.x + 1;
			start_indices.y = b_len - start_indices.y + 1;
			// System.out.println("start_indices=" + start_indices);
			/*
			 * Use Myers and Miller recursive, divide-and-conquer strategy to
			 * align the subsequences bracketed by the local alignment using
			 * Gotoh's linear-space conversion cost.
			 */
			diff(start_indices.x - 1, start_indices.y - 1, end_indices.x - start_indices.x + 1,
					end_indices.y - start_indices.y + 1);
			display(n, start_indices.x, end_indices.x, start_indices.y, end_indices.y);
			/*
			 * Global Alignment.
			 */
			// int a_len = a_arr.length, b_len = b_arr.length;
			// diff(0, 0, a_len, b_len);
			// display(n, 1, a_len, 1, b_len);
		}
	}

	static void init(String A, String B) {
		linear.A = A.toLowerCase();
		linear.B = B.toLowerCase();
		a_arr = linear.A.toCharArray();
		b_arr = linear.B.toCharArray();
		int m = a_arr.length, n = b_arr.length;
		rev_a = new char[m];
		rev_b = new char[n];
		for (int i = 0; i < m; i++) {
			rev_a[m - i - 1] = a_arr[i];
		}
		for (int i = 0; i < n; i++) {
			rev_b[n - i - 1] = b_arr[i];
		}
		/*
		 * As defined in Myers and Miller (1988), but maybe erroneous.
		 * Reasoning: The vector is supposed to replace an |A|x|B| substitution
		 * matrix, by only maintaining a row of interest. Rows are of length |B|
		 * = n, i.e. aligning some A[1...i] to B.
		 */
		// cc = new double[m + 1];
		// dd = new double[m + 1];
		cc = new double[n + 1];
		dd = new double[n + 1];
		rr = new double[n + 1];
		ss = new double[n + 1];
		s = new LinkedList<Integer>();
	}

	static void display(int n, int a_start, int a_end, int b_start, int b_end) {
		int a_ctr = 0, b_ctr = 0;
		String a = "";
		String b = "";
		String r = "";
		if (b_start > a_start) {
			int spaces = b_start - a_start;
			for (int i = 0; i < b_start - 1; i++) {
				b += b_arr[b_ctr];
				if (i >= spaces) {
					a += a_arr[a_ctr];
					a_ctr++;
				} else {
					a += " ";
				}
				r += " ";
				b_ctr++;
			}
		} else if (a_start > b_start) {
			int spaces = a_start - b_start;
			for (int i = 0; i < a_start - 1; i++) {
				a += a_arr[a_ctr];
				if (i >= spaces) {
					b += b_arr[b_ctr];
					b_ctr++;
				} else {
					b += " ";
				}
				r += " ";
				a_ctr++;
			}
		} else {
			for (int i = 0; i < a_start - 1; i++) {
				a += a_arr[a_ctr];
				b += b_arr[b_ctr];
				r += " ";
				a_ctr++;
				b_ctr++;
			}
		}
		int length = 0, aligned = 0, gaps = 0;
		for (int i : s) {
			if (i < 0) {
				for (int j = 0; j < Math.abs(i); j++) {
					a += a_arr[a_ctr];
					b += " ";
					r += "-";
					a_ctr++;
					length++;
				}
				gaps++;
			} else if (i > 0) {
				for (int j = 0; j < i; j++) {
					a += " ";
					b += b_arr[b_ctr];
					r += "-";
					b_ctr++;
					length++;
				}
				gaps++;
			} else {
				a += a_arr[a_ctr];
				b += b_arr[b_ctr];
				r += ((a_arr[a_ctr] == b_arr[b_ctr]) ? "!" : "|");
				a_ctr++;
				b_ctr++;
				if (a_arr[a_ctr - 1] == b_arr[b_ctr - 1]) {
					aligned++;
				}
				length++;
			}
		}
		int a_len = a_arr.length, b_len = b_arr.length;
		for (int i = 0; i < Math.max(a_len - a_end, b_len - b_end); i++) {
			if (a_ctr < a_len) {
				a += a_arr[a_ctr];
				a_ctr++;
			} else {
				a += " ";
			}
			if (b_ctr < b_len) {
				b += b_arr[b_ctr];
				b_ctr++;
			} else {
				b += " ";
			}
			r += " ";
		}
		String d = "";
		for (int i = 0; i < r.length(); i++) {
			if ((i + 1) % 10 == 0) {
				d += ":";
			} else if ((i + 1) % 5 == 0) {
				d += ".";
			} else {
				d += " ";
			}
		}
		System.out.println("#=======================================");
		System.out.println("#");
		System.out.println("# Gap penalty: " + sw.alpha);
		System.out.println("# Extension penalty: " + sw.beta);
		System.out.println("# Match score: " + sw.match);
		System.out.println("# Mismatch score: " + sw.mismatch);
		System.out.println("#");
		System.out.println("# Length: " + length);
		System.out.printf("# Identity: %d/%d (%.1f%%)\n", aligned, length, ((double) aligned / length) * 100);
		System.out.printf("# Gaps: %d/%d (%.1f%%)\n", gaps, length, ((double) gaps / length) * 100);
		System.out.println("# Score: " + sw.score);
		System.out.println("#");
		System.out.println("#=======================================\n\n");
		int i = r.length(), ctr = 0;
		while (i >= n) {
			String c = new Integer(ctr).toString();
			int c_len = c.length();
			String sp = new String(new char[c_len]).replace("\0", " ");
			System.out.println(c + d.substring(r.length() - i, r.length() - i + n));
			System.out.println(sp + b.substring(r.length() - i, r.length() - i + n));
			System.out.println(sp + r.substring(r.length() - i, r.length() - i + n));
			System.out.println(sp + a.substring(r.length() - i, r.length() - i + n));
			System.out.println();
			i -= n;
			ctr += n;
		}
		if (d.length() - (r.length() - i) > 0) {
			String c = new Integer(ctr).toString();
			int c_len = c.length();
			String sp = new String(new char[c_len]).replace("\0", " ");
			System.out.println(c + d.substring(r.length() - i));
			System.out.println(sp + b.substring(r.length() - i));
			System.out.println(sp + r.substring(r.length() - i));
			System.out.println(sp + a.substring(r.length() - i));
			System.out.println();
		}
	}

	static void diff(int a_start, int b_start, int m, int n) {
		diff_recurs(a_start, b_start, m, n, g, g);
	}

	static void diff_recurs(int a_start, int b_start, int m, int n, double tb, double te) {
		// System.out.printf("a_start=%d b_start=%d m=%d n=%d\n", a_start,
		// b_start, m, n);
		if (n == 0) {
			if (m > 0) {
				// System.out.println("delete A=" + A.substring(a_start, a_start
				// + m));
				s.addLast(-m);
			}
		} else if (m == 0) {
			// System.out.println("insert B=" + B.substring(b_start, b_start +
			// n));
			s.addLast(n);
		} else if (m == 1) {
			int ctr = 2;
			double cost = (Math.min(tb, te) + h) + gap(n);
			boolean type1 = true;
			s.addLast(n);
			s.addLast(-1);
			for (int j = 1; j <= n; j++) {
				double conv2 = gap(j - 1) + w(a_arr[a_start], b_arr[b_start + j - 1]) + gap(n - j);
				if (conv2 < cost) {
					for (int x = 0; x < ctr; x++) {
						s.removeLast();
					}
					ctr = 0;
					cost = conv2;
					if (j - 1 > 0) {
						s.addLast(j - 1);
						ctr++;
					}
					s.addLast(0);
					ctr++;
					if (n - j > 0) {
						s.addLast(n - j);
						ctr++;
					}
				}
			}
			// System.out.println("conversion of cost " + cost);
		} else {
			int i0 = m / 2;
			computeCost(a_start, b_start, i0, n, cc, dd, g, true);
			computeCost(a_start, b_start, m - i0, n, rr, ss, g, false);
			int j0 = 0;
			double t1 = cc[0] + rr[n], t2 = dd[0] + ss[n] - g;
			boolean type1 = (t1 < t2) ? true : false;
			double mid = Math.min(t1, t2);
			for (int j = 1; j <= n; j++) {
				t1 = cc[j] + rr[n - j];
				t2 = dd[j] + ss[n - j] - g;
				double j_curr = Math.min(t1, t2);
				if (j_curr < mid) {
					j0 = j;
					mid = j_curr;
					type1 = (t1 < t2) ? true : false;
				}
			}
			if (type1) {
				diff_recurs(a_start, b_start, i0, j0, tb, g);
				diff_recurs(a_start + i0, b_start + j0, m - i0, n - j0, g, te);
			} else {
				diff_recurs(a_start, b_start, i0 - 1, j0, tb, 0);
				// System.out.println("delete A[i0 - 1]A[i0]=" + a_arr[i0 - 1] +
				// a_arr[i0]);
				s.addLast(-2);
				diff_recurs(a_start + i0 + 1, b_start + j0, m - i0 - 1, n - j0, 0, te);
			}
		}
	}

	static void computeCost(int a_s, int b_s, int m, int n, double cc[], double dd[], double t0, boolean forward) {
		cc[0] = 0;
		double t = g;
		for (int j = 1; j <= n; j++) {
			t += h;
			cc[j] = t;
			dd[j] = t + g;
		}
		t = t0;
		for (int i = 1; i <= m; i++) {
			double s, c, e;
			s = cc[0];
			t += h;
			c = t;
			cc[0] = c;
			e = t + g;
			for (int j = 1; j <= n; j++) {
				e = Math.min(e, c + g) + h;
				dd[j] = Math.min(dd[j], cc[j] + g) + h;
				if (forward) {
					c = Math.min(dd[j], Math.min(e, s + w(a_arr[a_s + i - 1], b_arr[b_s + j - 1])));
				} else {
					c = Math.min(dd[j], Math.min(e, s + w(rev_a[a_s + i - 1], rev_b[b_s + j - 1])));
				}
				s = cc[j];
				cc[j] = c;
			}
		}
		dd[0] = cc[0];
	}

	static double gap(int k) {
		if (k > 0) {
			return g + h * k;
		}
		return 0;
	}

	static double w(char a, char b) {
		return (a == b) ? match : mismatch;
	}
}