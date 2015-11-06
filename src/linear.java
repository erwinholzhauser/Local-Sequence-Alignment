
public class linear {

	public linear(String A, String B) {
		this.A = A;
		this.B = B;

		int m = A.length(), n = B.length();
		cc = new double[m + 1];
		dd = new double[m + 1];
		rr = new double[n + 1];
		ss = new double[n + 1];
	}

	static String A, B;
	static double g = 2, h = 0.5;
	static double cc[], dd[], rr[], ss[];

	public static void main(String args[]) {

		// Example 1 from Myers and Miller (1987).
		// A = "agtac";
		// B = "aag";
		// int m = A.length(), n = B.length();
		// cc = new double[n + 1];
		// dd = new double[n + 1];
		// computeCost(m, n, cc, dd, g);
		// for (double d : cc)
		// System.out.print(d + " ");
		// System.out.print("\n");
		// for (double d : dd)
		// System.out.print(d + " ");
		// System.out.print("\n");

	}

	static void computeCost(int m, int n, double cc[], double dd[], double t0) {
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
				c = Math.min(dd[j], Math.min(e, s + w(A.charAt(i - 1), B.charAt(j - 1))));
				s = cc[j];
				cc[j] = c;
			}
		}
	}

	static double gap(int k) {
		return g + h * k;
	}

	static int w(char a, char b) {
		return (a == b) ? 0 : 1;
	}
}
