//Lab 2 B
//Zachary Whitney, zdw9, ID: 3320178
//CS 449 Tues 930AM Recitation

#include <stdio.h>

int main()

{
	int weight;
	printf("Please enter the weight you'd like to convert: ");
	scanf("%d", &weight);

	printf("\nHere is the weight on other planets:\n\n");
	printf("Mercury\t\t%d lbs\n", (int)(weight * .38));
	printf("Venus\t\t%d lbs\n", (int)(weight * .91));
	printf("Mars\t\t%d lbs\n", (int)(weight * .38));
	printf("Jupiter\t\t%d lbs\n", (int)(weight * 2.54));
	printf("Saturn\t\t%d lbs\n", (int)(weight * 1.08));
	printf("Uranus\t\t%d lbs\n", (int)(weight * .91));
	printf("Neptune\t\t%d lbs\n", (int)(weight * 1.19));
	return 0;
}
