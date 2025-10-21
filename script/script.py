import pandas as pd
import matplotlib.pyplot as plt

plt.figure(figsize=(10,8))
df = pd.read_csv("../src/io/output/GA/A-n32-k5_GA_1761078192579.csv", sep=';')
plt.plot(df["generation"], df["best"], label="Best")
plt.plot(df["generation"], df["average"], label="Average")
plt.plot(df["generation"], df["worst"], label="Worst")
plt.xlabel("Generation")
plt.ylabel("Fitness")
plt.legend()

plt.savefig("Best.png")