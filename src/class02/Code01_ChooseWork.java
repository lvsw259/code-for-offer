package class02;

import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;

public class Code01_ChooseWork {

	public static class Job {
		public int money;
		public int hard;

		public Job(int m, int h) {
			money = m;
			hard = h;
		}
	}

	public static class JobComparator implements Comparator<Job> {
		@Override
		public int compare(Job o1, Job o2) {
			return o1.hard != o2.hard ? (o1.hard - o2.hard) : (o2.money - o1.money);
		}
	}

	public static int[] getMoneys(Job[] job, int[] ability) {
		Arrays.sort(job, new JobComparator());
		// key : 难度   value：报酬
		TreeMap<Integer, Integer> map = new TreeMap<>();
		map.put(job[0].hard, job[0].money);
		// pre : 上一份进入map的工作
		Job pre = job[0];
		for (int i = 1; i < job.length; i++) {
			if (job[i].hard != pre.hard && job[i].money > pre.money) {
				pre = job[i];
				map.put(pre.hard, pre.money);
			}
		}
		int[] ans = new int[ability.length];
		for (int i = 0; i < ability.length; i++) {
			// ability[i] 当前人的能力 <= ability[i]  且离它最近的
			Integer key = map.floorKey(ability[i]);
			ans[i] = key != null ? map.get(key) : 0;
		}
		return ans;
	}
	//test for lvsw
//	public static class Job {
//		int hard;
//		int money;
//		public Job(int hard,int money){
//			this.hard=hard;
//			this.money=money;
//		}
//	}
//	public static class JobComparator implements Comparator<Job>{
//
//		@Override
//		public int compare(Job o1, Job o2) {
//			return o1.hard!=o2.hard ?o1.hard-o2.hard:o2.money-o1.money;
//		}
//	}
//	public static int[] getMoneys(Job[] job, int[] ability) {
//		JobComparator comparator = new JobComparator();
//		Arrays.sort(job,comparator);
//		TreeMap<Integer, Job> jobMap = new TreeMap<>();
//		jobMap.put(job[0].hard,job[0]);
//		Job pre = job[0];
//		for (int i = 1; i < job.length; i++) {
//			if(job[i].hard!=pre.hard && job[i].money>pre.money){
//				jobMap.put(job[i].hard,job[i]);
//				pre = job[i];
//			}
//		}
//		int[] ans = new int[ability.length];
//		for (int i = 0; i < ability.length; i++) {
//			Integer key = jobMap.floorKey(ability[i]);
//			ans[i]=key==null?0:jobMap.get(key).money;
//		}
//		return ans;
//	}


}
