package GUI.helpers2;

public class result implements Comparable<result>{
		private String combination;
		private Integer counter;
		
		public result(String v_combination, Integer v_counter){
			super();
			this.combination = v_combination;
			this.counter = v_counter;
		}
		
		public String getCombination(){
			return combination;
		}
		public Integer getCounter(){
			return counter;
		}

		@Override
		public int compareTo(result rs) {
			int compareCounter = ((result) rs).getCounter(); 
			
			//ascending order
			return this.counter - compareCounter;
		}
		
	}
