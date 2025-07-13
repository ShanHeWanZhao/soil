package com.github.soil.datastructures.sparsearray;

import org.junit.Test;

/**
 *
 *
 * 稀疏数组：
 *		（1）记录数组一共有几行几列，有多少个不同的值，所以为固定3列
 *		（2）把具有不同值的元素的行和列及值记录在一个小规模的数组中（某一行），从而缩小程序的规模
 *
 * @author Trd
 * @date 2020-02-27 13:40
 */
public class SparseArrayDemo {

	@Test
	public void chessToSparseTest(){
		int[][] chessArr = new int[6][4];
		chessArr[2][3] = 1;
		chessArr[4][1] = 1;
		chessArr[3][2] = 2;
		chessArr[1][0] = 2;
		System.out.println("普通二维数组");
		for (int[] first : chessArr){
			for (int item : first){
				System.out.print("   "+item);
			}
			System.out.println();
		}
		System.out.println("=========================");
		int[][] sparseArr = toSparseArray(chessArr);
		for (int[] first : sparseArr){
			for (int item : first){
				System.out.print("   "+item);
			}
			System.out.println();
		}

	}

	@Test
	public void sparseArrToChessTest(){
		int[][] sparseArr = new int[3][3];
		sparseArr[0][0] = 7;
		sparseArr[0][1] = 8;
		sparseArr[0][2] = 2;
		sparseArr[1][0] = 5;
		sparseArr[1][1] = 4;
		sparseArr[1][2] = 1;
		sparseArr[2][0] = 3;
		sparseArr[2][1] = 7;
		sparseArr[2][2] = 2;
		// 打印稀疏数组
		for (int[] first : sparseArr){
			for (int item : first){
				System.out.print("   "+item);
			}
			System.out.println();
		}
		System.out.println("=======================");
		int[][] chessArray = sparseArrayToChessArray(sparseArr);
		for (int[] first : chessArray){
			for (int item : first){
				System.out.print("   "+item);
			}
			System.out.println();
		}
	}

	/**
	 * 普通二维数组转为稀疏数组
	 * @param chessArr 二维数组
	 * @return
	 */
	public int[][] toSparseArray(int[][] chessArr){
		// 二维数组的行数
		int rowCount = chessArr.length;
		// 二维数组的列数
		int colCount = 0;
		// 二维数组中不为0的个数和
		int sum = 0;
		// 遍历数组，计算不为0的和并打印
		for (int[] firstArr : chessArr){
			colCount = firstArr.length;
			for (int item : firstArr){
				if (item != 0){
					sum++;
				}
			}
		}
		// 稀疏数组的行标
		int rowMark = 0;
		// 构建稀疏数组,列数肯定为3
		int[][] sparesArr = new int[sum + 1][3];
		// 稀疏数组第一行赋值
		sparesArr[0][0] = rowCount;
		sparesArr[0][1] = colCount;
		sparesArr[0][2] = sum;
		// 稀疏数组其他行赋值
		for (int i = 0;i < rowCount; i++){
			for (int j = 0;j < colCount; j++){
				if (chessArr[i][j] != 0){
					rowMark++;
					sparesArr[rowMark][0] = i;
					sparesArr[rowMark][1] = j;
					sparesArr[rowMark][2] = chessArr[i][j];
				}
			}
		}
		return sparesArr;
	}

	/**
	 * 稀疏数组转为普通二维数组
	 * @param sparseArray 稀疏数组
	 * @return 棋盘演变的二维数组
	 */
	public int[][] sparseArrayToChessArray(int[][] sparseArray){
		int[][] chessArr = new int[sparseArray[0][0]][sparseArray[0][1]];
		for (int i = 1;i < sparseArray.length; i++){
			chessArr[sparseArray[i][0]][sparseArray[i][1]] = sparseArray[i][2];
		}
		return chessArr;
	}
}
