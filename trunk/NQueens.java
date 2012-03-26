//import SableJBDD.bdd.*; 
//
///**
// * @author John Whaley
// * @modified by Feng Qian
// */
//public class NQueens {
//    public static JBddManager B;
//
//    static int N; /* Size of the chess board */
//    static JBDD[][] X; /* BDD variable array */
//    static JBDD queen; /* N-queen problem express as a BDD */
//
//    public static void main(String[] args) {
//        if (args.length != 1) {
//            System.err.println("USAGE:  java NQueens N");
//            return;
//        }
//        N = Integer.parseInt(args[0]);
//        if (N <= 0) {
//            System.err.println("USAGE:  java NQueens N");
//            return;
//        }
//
//        /* Initialize with reasonable nodes and cache size and NxN variables */
//        int numberOfNodes = (int) (Math.pow(4.4, N-6))*1000;
//        int cacheSize = 1000;
//        numberOfNodes = Math.max(1000, numberOfNodes);
//
//        long starttime, endtime;
//
//        starttime = System.currentTimeMillis();
//        B = new JBddManager(N*N);
//        endtime = System.currentTimeMillis();
//        System.out.println("Create JBddManager : "+(endtime - starttime)/1000.+" seconds");
//        
//        starttime = System.currentTimeMillis();
//        queen = B.ONE();
//
//        int i, j;
//
//        /* Build variable array */
//        X = new JBDD[N][N];
//        for (i = 0; i < N; i++)
//            for (j = 0; j < N; j++)
//                X[i][j] = B.posBddOf(B.getIthVariable(i * N + j));
//
//        /* Place a queen in each row */
//        for (i = 0; i < N; i++) {
//            JBDD e = B.ZERO();
//            for (j = 0; j < N; j++) {
//                e.orWith(X[i][j]);
//            }
//            queen.andWith(e);
//        }
//
//        /* Build requirements for each variable(field) */
//        for (i = 0; i < N; i++)
//            for (j = 0; j < N; j++) {
//                System.out.println("Adding position " + i + "," + j);
//                System.out.flush();
//                build(i, j);
//            }
//
//        /* Print the results */
//       // System.out.println("There are " + (long) queen.satCount() + " solutions.");
//       //  BDD solution = queen.satOne();
//       // System.out.println("Here is "+(long) solution.satCount() + " solution:");
//       // solution.printSet();
//       // System.out.println();
//
//      // solution.free();
//      //  freeAll();
//      //  B.done();
//
//        endtime = System.currentTimeMillis();
//        System.out.println("Solving: "+(endtime - starttime)/1000.+" seconds");
//
//        System.out.println("queen has "+queen.size()+" nodes");
//       
////        queen.toDotGraph(System.err, "queen"+N);
//
//        B.reportCacheStatistics(System.out);
//    }
//
///*
//    static void freeAll() {
//        queen.free();
//        for (int i = 0; i < N; i++)
//            for (int j = 0; j < N; j++)
//                X[i][j].free();
//    }
//*/
//    static void build(int i, int j) {
//        JBDD a = B.ONE(), b = B.ONE(), c = B.ONE(), d = B.ONE();
//        int k, l;
//
//        /* No one in the same column */
//        for (l = 0; l < N; l++) {
//            if (l != j) {
//                JBDD u = X[i][l].nand(X[i][j]);
//                a.andWith(u);
//            }
//        }
//
//        /* No one in the same row */
//        for (k = 0; k < N; k++) {
//            if (k != i) {
//                JBDD u = X[i][j].nand(X[k][j]);
//                b.andWith(u);
//            }
//        }
//
//        /* No one in the same up-right diagonal */
//        for (k = 0; k < N; k++) {
//            int ll = k - i + j;
//            if (ll >= 0 && ll < N) {
//                if (k != i) {
//                    JBDD u = X[i][j].nand(X[k][ll]);
//                    c.andWith(u);
//                }
//            }
//        }
//
//        /* No one in the same down-right diagonal */
//        for (k = 0; k < N; k++) {
//            int ll = i + j - k;
//            if (ll >= 0 && ll < N) {
//                if (k != i) {
//                    JBDD u = X[i][j].nand(X[k][ll]);
//                    d.andWith(u);
//                }
//            }
//        }
//        
//        c.andWith(d);
//        b.andWith(c);
//        a.andWith(b);
//        queen.andWith(a);
//    }
//
//}
