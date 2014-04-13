package wordcloud;

import java.util.* ;
import java.io.* ;
import java.sql.*;
import java.lang.*;

class trend{	

        public boolean isSuccessful(){
            return true;
        }
    
	long timeDifference(Timestamp t1, Timestamp t2){
		long startMilliseconds = t1.getTime();
		long endMilliseconds = t2.getTime();
		return (endMilliseconds - startMilliseconds);
	}

	void getMomentum(String hashtag, int S, int L, Timestamp start, Timestamp end,database obj){

		Timestamp step = new Timestamp(0);

		long T = timeDifference(start,end);
		int n1,n2;
		int frame1,size;
		Timestamp start_frame = new Timestamp(0);
		Timestamp end_frame = new Timestamp(0);

		

		long []freq_shortwindow = new long[(int)(T/S)];
		long []freq_longwindow = new long[(int)(T/L)];
		double []MA_short = new double[(int)(T/S)];
		double []MA_long = new double[(int)(T/L)];
		double []Momentum = new double[(int)(T/L)];
		double []Momentum_avg = new double[(int)(T/L)];
		double alpha = 0.1;


		System.out.println("FOR SMALL WINDOWS **************");
		for(n1 = 0; n1 < (int)(T/S); n1++){

			if(n1 < S){
				frame1 = 0;
				size = n1 + 1;
			}
			else{
				frame1 = n1 - S + 1;
				size = S;
				System.out.println("*********SHORT********");
			}
			start_frame.setTime(start.getTime() + (int)(n1*S));
			end_frame.setTime(start.getTime() +(int)((n1+1)*S)); 
			freq_shortwindow [n1] = obj.getFreq(hashtag, start_frame, end_frame);
			System.out.println("START = "+start_frame+" END = "+end_frame+"FREQ = "+freq_shortwindow [n1]);
			// debug here check whether its the expected window or not --- works fine and retrieval is also ok
			MA_short[n1] = 0.0;
			for(;frame1 <= n1;frame1++){
				MA_short[n1] = MA_short[n1] + freq_shortwindow[frame1]; 
			}
			MA_short[n1] = MA_short[n1] / (double)(size);			
			// System.out.println("MASHORT["+n1+"] = "+MA_short[n1]);
		}   
		System.out.println("FOR LARGE WINDOWS **************");
		for(n2 = 0; n2 < (int)(T/L); n2++){

			if(n2 < L){
				frame1 = 0;
				size = n2 + 1;
			}
			else{
				frame1 = n2 - L + 1;
				size = L;
				System.out.println("*********LONG********");
			}
			start_frame.setTime(start.getTime() + n2*L);
			end_frame.setTime(start.getTime() +(n2+1)*L);
			// debug here check whether its the expected window or not ---- works fine
			freq_longwindow [n2] = obj.getFreq(hashtag, start_frame, end_frame);
			System.out.println("START = "+start_frame+" END = "+end_frame+"FREQ = "+freq_longwindow [n2]);
			MA_long[n2] = 0.0;
			for(;frame1 <= n2;frame1++){
				MA_long[n2] = MA_long[n2] + freq_longwindow[frame1]; 
			}
			MA_long[n2] = MA_long[n2] / (double)(size);			
			// System.out.println("MALONG["+n2+"] = "+MA_long[n2]);
		}
		for(n2 =0;n2 < (int)(T/L); n2++){
			if(MA_long[n2]<0.000000000001||MA_long[n2] == 0.0){
				continue;
			}else{
				break;
			}
		}
		for(;n2 < (int)(T/L); n2++){

			Momentum[n2] = MA_short[n2] - Math.pow(MA_long[n2],alpha);
			for(frame1 = 0; frame1 <= n2;frame1++){
				Momentum_avg[n2] = Momentum_avg[n2] + Momentum[frame1]; // need to handle .... need to ensure
				// that they are non zero ... once they are non zero they will always be the same ... so start from where they are 
				// both non zero /.... remove the infinity/nan cases //TODO SIKHAR
			}
			Momentum_avg[n2] = Momentum_avg[n2] / (double)(n2);
			System.out.println("MomentumAVG = "+Momentum_avg[n2]);
		}



	}

	void getUserCorr(String hashtag, int numUsers, Timestamp start, Timestamp end,database obj){

		int i;
		double []corr = new double[numUsers];
		long A, B, C, D;
		double fact1, fact2, fact3;

		for(i = 0;i < numUsers;i++){

			A = obj.getuserinfo_A(hashtag, i, start, end);
			B = obj.getuserinfo_B(hashtag, i, start, end);
			C = obj.getuserinfo_C(hashtag, i, start, end);
			D = obj.getuserinfo_D(hashtag, i, start, end);

			// (A+B+C+D)(AD-BC)2/(A+B)(C+D)(A+C)(B+D)

			fact1 = A + B +C +D;
			fact2 = (A*D - B*C) * (A*D - B*C);
			fact3 = (A+B) * (B+C) * (C+D) * (D+A);

			corr[i] = (fact1)*(fact2)/(fact3);
		}
	}

	public trend(String hashtag, int numUsers, Timestamp start, Timestamp end, int S, int L){
		database obj = new database();
		getMomentum(hashtag,S,L,start,end,obj);
		getUserCorr(hashtag,numUsers,start,end,obj);

	}
//	public static void main(String []args){
//
//		String hashtag = "London2012";
//		int numUsers = 100;
//		Calendar cal = GregorianCalendar.getInstance();
//		cal.set(Calendar.DAY_OF_MONTH, 1);// I might have the wrong Calendar constant...
//		cal.set(Calendar.MONTH, 7-1);// -1 as month is zero-based
//		cal.set(Calendar.YEAR, 2012);
//		Timestamp start = new Timestamp(cal.getTimeInMillis());
//		// Calendar cal = GregorianCalendar.getInstance();
//		cal.set(Calendar.DAY_OF_MONTH, 31);// I might have the wrong Calendar constant...
//		cal.set(Calendar.MONTH, 8-1);// -1 as month is zero-based
//		cal.set(Calendar.YEAR, 2012);
//		Timestamp end   = new Timestamp(cal.getTimeInMillis());
//		// MAX INT 2147483647
//		int S = 100000;
//		int L = 300000;
//		System.out.println(start);
//		System.out.println(end);
//		System.out.println(S);
//		System.out.println(L);
//		trend obj = new trend(hashtag,numUsers,start,end,S,L);
//	}
}

