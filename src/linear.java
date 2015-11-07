import java.util.Collections;

public class linear {

	public linear(String A, String B) {
		this.A = A;
		this.B = B;

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
	}

	static String A, B;
	static char a_arr[], b_arr[], rev_a[], rev_b[];
	static double g = 2, h = 0.5;
	static double cc[], dd[], rr[], ss[];

	public static void main(String args[]) {
		new linear("agtac", "aag");
		
		 // Example 1 from Myers and Miller (1987).
//		 computeCost(0, 0, A.length(), B.length(), cc, dd, g, true);
//		 for (double d : cc)
//		 System.out.print(d + " ");
//		 System.out.print("\n");
//		 for (double d : dd)
//		 System.out.print(d + " ");
//		 System.out.print("\n");
		
		// Example 2 from Myers and Miller (1987).
		 new linear("agtac", "aag");
		 diff(0, 0, A.length(), B.length());
	}

	static void diff(int a_start, int b_start, int m, int n) {
		diff_recurs(a_start, b_start, m, n, g, g);
	}

	static void diff_recurs(int a_start, int b_start, int m, int n, double tb, double te) {
		// System.out.printf("a_start=%d b_start=%d m=%d n=%d tb=%.2f te=%.2f\n", a_start, b_start, m, n, tb, te);
		if (n == 0) {
			if (m > 0) {
				System.out.println("delete A=" + A.substring(a_start, a_start + m));
			}
		} else if (m == 0) {
			System.out.println("insert B=" + B.substring(b_start, b_start + n));
		} else if (m == 1) {
			double cost = (Math.min(tb, te) + h) + gap(n);
			for (int j = 1; j <= n; j++) {
				double conv2 = gap(j - 1) + w(a_arr[0], b_arr[j - 1]) + gap(n - j);
				cost = Math.min(cost, conv2);
			}
			System.out.println("conversion of cost " + cost);
		} else {
			int i0 = m / 2;
			computeCost(a_start, b_start, i0, n, cc, dd, g, true);
			computeCost(a_start, b_start, m - i0, n, rr, ss, g, false);
			
			// System.out.print("cc = ");
			// for (double d : cc)
			// System.out.print(d + " ");
			// System.out.print("\n");
			// System.out.print("rr = ");
			// for (double d : rr)
			// System.out.print(d + " ");
			// System.out.print("\n");
			//
			// System.out.print("dd = ");
			// for (double d : dd)
			// System.out.print(d + " ");
			// System.out.print("\n");
			// System.out.print("ss = ");
			// for (double d : ss)
			// System.out.print(d + " ");
			// System.out.print("\n");
			
			int j0 = 0;
			double t1 = cc[0] + rr[n], t2 = dd[0] + ss[n] - g;
			boolean type1 = (t1 < t2) ? true : false;
			double mid = Math.min(t1, t2);

			// System.out.printf("j=0 type 1 midpoint=%.1f type2 midpoint=%.1f\n", t1, t2);
			for (int j = 1; j <= n; j++) {
				t1 = cc[j] + rr[n - j];
				t2 = dd[j] + ss[n - j] - g;
				// System.out.printf("j=%d type 1 midpoint=%.1f type2 midpoint=%.1f\n", j, t1, t2);
				double j_curr = Math.min(t1, t2);
				if(j_curr < mid) {
					j0 = j;
					mid = j_curr;
					type1 = (t1 < t2) ? true : false;
				}
			}
			// System.out.println("j0=" + j0 + " type1=" + type1);

			if (type1) {
				diff_recurs(a_start, b_start, i0, j0, tb, g);
				diff_recurs(a_start + i0 - 1, b_start + j0 - 1, m - i0, n - j0, g, te);
			} else {
				diff_recurs(a_start, b_start, i0 - 1, j0, tb, 0);
				System.out.println("delete A[i0 - 1]A[i0]=" + a_arr[i0 - 1] + a_arr[i0]);
				diff_recurs(a_start + i0, b_start + j0 - 1, m - i0 - 1, n - j0, 0, te);
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
		return g + h * k;
	}

	static int w(char a, char b) {
		return (a == b) ? 0 : 1;
	}
}
