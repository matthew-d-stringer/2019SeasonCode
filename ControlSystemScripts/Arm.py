from control.matlab import *
import numpy as np

dt = 0.01

A = np.matrix('0 1; 1 0')

B = np.matrix('0; 1')

C = np.matrix('1 0; 1 0')
D = np.matrix('0; 0')

sys = ss(A,B,C,D)
sysd = c2d(sys, dt)

Q = np.matrix('10 0; 0 0.8')
R = 0.1
K = lqr(sysd.A, sysd.B, Q, R)
print(K)