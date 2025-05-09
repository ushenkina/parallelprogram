#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <omp.h>
#include <sys/time.h>

void *heavy_task(int thread_num) {
    printf("OpenMP threads: %d\n", omp_get_num_threads());
    int limit = 1e8;
    for (int i = 0; i < limit; i++) {
      sqrt(i);
    }
    printf("\tThread #%d finished\n", thread_num);
  }
  
  void openmp(int thread_num) {
    #pragma omp parallel for num_threads(thread_num)
    for (int i = 0; i < thread_num; i++) {
      printf("MAIN: starting thread %d\n", i);
      heavy_task(i);
    }
  }
  
  int main(int argc, char** argv) {
    struct timeval start, end;
    gettimeofday(&start, NULL);
    int threads_num = atoi(argv[1]);
    openmp(threads_num);
    gettimeofday(&end, NULL);
    long seconds = end.tv_sec - start.tv_sec;
    long micros = end.tv_usec - start.tv_usec;
    if (micros < 0) {
        seconds--;
        micros += 1000000;
    }
  
    // Конвертируем время в миллисекунды
    double time = seconds * 1000.0 + micros / 1000.0;
    printf("%d потоков openmp %.4f мс \n", threads_num, time);
    return 0;
  }