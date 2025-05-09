#include <stdio.h>
#include <stdlib.h>
#include <time.h>

void loop(float a[], float b[], float c[]) 
{
    for (int i=0; i<4; i++) {
        c[i] = a[i] * b[i];
    }
}

int main(int argc, char** argv) {
    clock_t start = clock();
    if (argc < 1) {
        printf("Не переданы все необходимые аргументы");
        return 1;
    }
    int iterations_num = atoi(argv[1]);
    float a[] = {300.0, 4.0, 4.0, 12.0};
    float b[] = {1.5, 2.5, 3.5, 4.5};
    float c[] = {0.0, 0.0, 0.0, 0.0};
    
    for (int i=0; i<iterations_num; i++) {
        loop(a, b, c);
    }
    clock_t end = clock();

    double time = ((double) (end-start) / CLOCKS_PER_SEC) * 1e3;
    printf("%d итераций %.4f мс \n", iterations_num, time);

    return 0;
}