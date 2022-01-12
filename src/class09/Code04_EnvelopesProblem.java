package class09;

import java.util.Arrays;
import java.util.Comparator;

// 本题测试链接 : https://leetcode.com/problems/russian-doll-envelopes/
public class Code04_EnvelopesProblem {

	public static int maxEnvelopes(int[][] matrix) {
		Envelope[] arr = sort(matrix);
		int[] ends = new int[matrix.length];
		ends[0] = arr[0].h;
		int right = 0;
		int l = 0;
		int r = 0;
		int m = 0;
		for (int i = 1; i < arr.length; i++) {
			l = 0;
			r = right;
			while (l <= r) {
				m = (l + r) / 2;
				if (arr[i].h > ends[m]) {
					l = m + 1;
				} else {
					r = m - 1;
				}
			}
			right = Math.max(right, l);
			ends[l] = arr[i].h;
		}
		return right + 1;
	}
	public static int maxEnvelopes1(int[][] matrix) {
		Envelope[] arr = sort(matrix);
		int[] ends = new int[matrix.length+1];
		ends[1] = arr[0].h;
		int len =1;
		int right = 0;
		int l = 0;
		int r = 0;
		int m = 0;
		for (int i = 1; i < arr.length; i++) {
			if(arr[i].h>ends[len]){
				ends[++len] = arr[i].h;
			}else {
				l = 0;
				r = len;
				int post =0;
				while (l <= r) {
					m = (l + r) / 2;
					if (arr[i].h > ends[m]) {
						post = m;
						l = m + 1;
					} else {
						r = m - 1;
					}
				}
				ends[post+1] = arr[i].h;
			}
		}
		return len;
	}

	public static class Envelope {
		public int l;
		public int h;

		public Envelope(int weight, int hight) {
			l = weight;
			h = hight;
		}
	}

	public static class EnvelopeComparator implements Comparator<Envelope> {
		@Override
		public int compare(Envelope o1, Envelope o2) {
			return o1.l != o2.l ? o1.l - o2.l : o2.h - o1.h;
		}
	}

	public static Envelope[] sort(int[][] matrix) {
		Envelope[] res = new Envelope[matrix.length];
		for (int i = 0; i < matrix.length; i++) {
			res[i] = new Envelope(matrix[i][0], matrix[i][1]);
		}
		Arrays.sort(res, new EnvelopeComparator());
		return res;
	}

}
