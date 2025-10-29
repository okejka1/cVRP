import pandas as pd
import matplotlib.pyplot as plt

# df = pd.read_csv("../src/io/output/GA/A-n32-k5_GA_1761078192579.csv", sep=';')
plt.figure(figsize=(14,12))

pop_size=500
cf=0.8
mf=0.3
e=0.05
maxGen=5000
t=6
df = pd.read_csv("A-n60-k9_GA_80swap_2inv.csv", sep=';')
plt.plot(df["generation"], df["best"], label="Best")
plt.plot(df["generation"], df["average"], label="Average")
plt.plot(df["generation"], df["worst"], label="Worst")

plt.xlabel("Generation", fontsize=14)
plt.ylabel("Cost", fontsize=14)
plt.title(f'A-n60-k9 (popSize={pop_size}, crossoverFactor={cf}, mutationFactor={mf}, elitismFactor={e}, maxGen={maxGen}, tour={t}')
plt.legend(fontsize=12)
plt.xticks(fontsize=12)
plt.yticks(fontsize=12)

plt.savefig(f'A-n60-k9_{pop_size}_{cf}_{mf}_{e}_{maxGen}_{t}.png', dpi=300)

