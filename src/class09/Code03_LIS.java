package class09;

// 本题测试链接 : https://leetcode.com/problems/longest-increasing-subsequence
public class Code03_LIS {

	public static int lengthOfLIS(int[] arr) {
		if (arr == null || arr.length == 0) {
			return 0;
		}
		int[] ends = new int[arr.length];
		ends[0] = arr[0];
		int right = 0;
		int l = 0;
		int r = 0;
		int m = 0;
		int max = 1;
		for (int i = 1; i < arr.length; i++) {
			l = 0;
			r = right;
			while (l <= r) {
				m = (l + r) / 2;
				if (arr[i] > ends[m]) {
					l = m + 1;
				} else {
					r = m - 1;
				}
			}
			right = Math.max(right, l);
			ends[l] = arr[i];
			max = Math.max(max, l + 1);
		}
		return max;
	}


	public static int lengthOfLIS1(int[] nums) {
		int len = 1, n = nums.length;
		if (n == 0) {
			return 0;
		}
		int[] d = new int[n + 1];
		d[len] = nums[0];
		for (int i = 1; i < n; ++i) {
			if (nums[i] > d[len]) {
				d[++len] = nums[i];
			} else {
				int l = 1, r = len, pos = 0; // 如果找不到说明所有的数都比 nums[i] 大，此时要更新 d[1]，所以这里将 pos 设为 0
				while (l <= r) {
					int mid = (l + r) >> 1;
					if (d[mid] < nums[i]) {
						pos = mid;
						l = mid + 1;
					} else {
						r = mid - 1;
					}
				}
				d[pos + 1] = nums[i];
			}
		}
		return len;
	}


}