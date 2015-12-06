import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;

public class SmithWaterman {

	public static double match = 2, mismatch = -3, alpha = 5, beta = 2, score;

	public static Point findEndIndices(String a, String b, boolean rev, Point end) {

		int m = a.length(), n = b.length();

		double s[] = new double[n + 1], f[] = new double[n + 1];
		for (int j = 0; j <= n; j++) {
			s[j] = 0;
			f[j] = 0;
		}

		double e, up, diagonal;
		double max_score = 0;
		Point max_index = new Point();
		for (int i = 1; i <= m; i++) {
			s[0] = e = 0;
			diagonal = 0;
			for (int j = 1; j <= n; j++) {
				up = s[j];
				e = Math.max(s[j - 1] - (alpha + beta), e - beta);
				f[j] = Math.max(s[j] - (alpha + beta), f[j] - beta);
				s[j] = Collections
						.max(Arrays.asList(diagonal + score(a.charAt(i - 1), b.charAt(j - 1)), e, f[j], (double) 0));
				diagonal = up;

				// For forward phase, want the left-most local alignment.
				if (!rev && s[j] > max_score) {
					max_score = s[j];
					max_index.setLocation(i, j);
				}

				// For the reverse phase, want the right-most local alignment.
				if (rev && i >= (m - end.x + 1) && j >= (n - end.y + 1) && s[j] >= max_score) {
					max_score = s[j];
					max_index.setLocation(i, j);
				}
				// System.out.printf(s[j] + " ");
			}
			// System.out.printf("\n");
		}
		// System.out.println("max_score=" + max_score);

		if (!rev) {
			score = max_score;
		}

		return max_index;
	}

	static double score(char a, char b) {
		return (a == b) ? match : mismatch;
	}
}
