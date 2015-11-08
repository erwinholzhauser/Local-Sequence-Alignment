import java.util.LinkedList;

import org.apache.commons.cli.Options;

public class linear {

	public linear(String A, String B) {
		init(A, B);
	}

	static String A, B;
	static char a_arr[], b_arr[], rev_a[], rev_b[];
	static double g = 10, h = 0.5;
	static double cc[], dd[], rr[], ss[];
	static LinkedList<Integer> s;
	
	public static void main(String args[]) {
		
		Options opt = new Options();
		
		opt.addOption("g", true, "Gap open penalty");
		opt.addOption("h", true, "Gap extension penalty");
		//opt.addOption("")
		
		// Examples from Myers and Miller (1987).
		new linear("gcgttcataaccggcgaggtacctagacattcccagagcgcctcgatatggacagaaatcgagcaacgacgactg",
		 		"ggcgtttcataccggcgaggactagagatcccagatgcagcctcgatataggaagaatcagcaacgatcggcatg");
		//new linear("agtac", "aag");
		diff(0, 0, A.length(), B.length());
		display(50);
	}
	
	static void init(String A, String B) {
		linear.A = A;
		linear.B = B;

		int m = A.length(), n = B.length();
		a_arr = A.toCharArray();
		b_arr = B.toCharArray();

		rev_a = new char[m];
		rev_b = new char[n];

		for (int i = 0; i < m; i++) {
			rev_a[m - i - 1] = a_arr[i];
		}

		for (int i = 0; i < n; i++) {
			rev_b[n - i - 1] = b_arr[i];
		}

		cc = new double[m + 1];
		dd = new double[m + 1];
		rr = new double[n + 1];
		ss = new double[n + 1];
		s = new LinkedList<Integer>();
	}
	
	static void display(int n) {
		int a_ctr = 0, b_ctr = 0;
		String a = "";
		String b = "";
		String r = "";
		for (int i : s) {
			if (i < 0) {
				for(int j = 0; j < Math.abs(i); j++) {
					a += a_arr[a_ctr];
					b += " ";
					r += "-";
					a_ctr++;
				}
			} else if (i > 0) {
				for(int j = 0; j < i; j++) {
					a += " ";
					b += b_arr[b_ctr];
					r += "-";
					b_ctr++;
				}
			} else {
				a += a_arr[a_ctr];
				b += b_arr[b_ctr];
				r += ((a_arr[a_ctr] == b_arr[b_ctr]) ? "!" : "|");
				a_ctr++; b_ctr++;
			}
		}
		
		String d = "";
		for(int i = 0; i < r.length(); i++) {
			if((i+1) % 10 == 0) {
				d += ":";
			} else if((i+1) % 5 == 0) {
				d += ".";
			} else {
				d += " ";
			}
		}
		
		int i = r.length(), ctr = 0;
		while(i >= n) {
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
		String c = new Integer(ctr).toString();
		int c_len = c.length();
		String sp = new String(new char[c_len]).replace("\0", " ");
		System.out.println(c + d.substring(r.length() - i));
		System.out.println(sp + b.substring(r.length() - i));
		System.out.println(sp + r.substring(r.length() - i));
		System.out.println(sp + a.substring(r.length() - i));
		System.out.println();
	}

	static void diff(int a_start, int b_start, int m, int n) {
		diff_recurs(a_start, b_start, m, n, g, g);
	}

	static void diff_recurs(int a_start, int b_start, int m, int n, double tb, double te) {
		if (n == 0) {
			if (m > 0) {
				// System.out.println("delete A=" + A.substring(a_start, a_start + m));
				s.addLast(-m);
			}
		} else if (m == 0) {
			// System.out.println("insert B=" + B.substring(b_start, b_start + n));
			s.addLast(n);
		} else if (m == 1) {
			int ctr = 2;
			double cost = (Math.min(tb, te) + h) + gap(n);
			boolean type1 = true;
			s.addLast(n); s.addLast(-1);
			for (int j = 1; j <= n; j++) {
				double conv2 = gap(j - 1) + w(a_arr[a_start], b_arr[b_start + j - 1]) + gap(n - j);
				if(conv2 < cost) {
					for(int x = 0; x < ctr; x++) {
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
				if(j_curr < mid) {
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
				// System.out.println("delete A[i0 - 1]A[i0]=" + a_arr[i0 - 1] + a_arr[i0]);
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
		if(k > 0) {
			return g + h * k;
		}
		return 0;
	}

	static int w(char a, char b) {
		return (a == b) ? 0 : 2;
	}
}
