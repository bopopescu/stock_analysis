'''
Created on Jun 14, 2016

@author: gsantiago
'''

import glob
import json
import pandas as pd
import numpy as np
import datetime
import matplotlib
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
from pandas.tools.plotting import scatter_matrix
from pandas.tools.plotting import parallel_coordinates
from pandas.tools.plotting import lag_plot
from pandas.tools.plotting import autocorrelation_plot
from pandas.tools.plotting import radviz

results_dir='/git_repos/github/stock_analysis/localdata/jsonresults'
files = glob.glob(results_dir+"/*.json")

#################Creating the data frame
data = []
for f in files:
    with open(f) as jsonFile:
        jsonData = json.load(jsonFile)
        data.append(
                    {'trainingSize': jsonData['trainingSizeInMonths'],
                     'windowSize': jsonData['windowSizeInMonths'],
                     'riskRate': jsonData['riskRate'],
                     'initialDate': pd.Timestamp(jsonData['initialDate']),
                     'finalDate': pd.Timestamp(jsonData['finalDate']),
                     'finalBalance': jsonData['finalBalance']
                     }
                    )
                   
df = pd.DataFrame(data)        
df = df.sort(columns=('finalDate'))

#################Ploting in 3D
x=df[df.windowSize==1][df.trainingSize==2][df.riskRate==0.01].index
y=df[df.windowSize==1][df.trainingSize==2][df.riskRate==0.01]['finalBalance']
z=df[df.windowSize==1][df.trainingSize==2][df.riskRate==0.01]['riskRate']

fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')
ax.scatter3D(x.values, y.values, z.values)

ax.set_xlabel('X Label')
ax.set_ylabel('Y Label')
ax.set_zlabel('Z Label')
ax.set_xticks(df.finalDate[x].apply(lambda d: d.strftime("%Y-%m-%d")).values)
plt.show()


#################Several plots in 2D
df.plot(subplots=True)
df.plot(x='finalDate', y='finalBalance')
df.hist(by=['windowSize', 'trainingSize'])
df.boxplot('finalBalance', by=['windowSize', 'trainingSize'])
scatter_matrix(df, alpha=0.2, diagonal='kde')
df.plot(x='finalDate', y='finalBalance', kind='kde')

parallel_coordinates(df, 'windowSize')
autocorrelation_plot(df.finalBalance)
radviz(df, 'finalBalance')
df.plot(colormap='jet')

#################More specific plots in 2D
f, (ax1, ax2) = plt.subplots(2, 3)
ax1[0].plot(df.groupby(['windowSize']).mean()['finalBalance'])
ax1[0].set_title('Window Size Mean')
ax1[0].set_ylim((5000, 15000))

ax2[0].plot(df.groupby(['windowSize']).sum()['finalBalance'])
ax2[0].set_title('Window Size Sum')

ax1[1].plot(df.groupby(['trainingSize']).mean()['finalBalance'])
ax1[1].set_title('Training Size Mean')
ax1[1].set_ylim((5000, 15000))

ax2[1].plot(df.groupby(['trainingSize']).mean()['finalBalance'])
ax2[1].set_title('Training Size Sum')

ax1[2].plot(df.groupby(['riskRate']).mean()['finalBalance'])
ax1[2].set_title('Risk Rate Mean')
ax1[2].set_ylim((5000, 15000))
ax1[2].set_xlim((0, 1))

ax2[2].plot(df.groupby(['riskRate']).mean()['finalBalance'])
ax2[2].set_title('Risk Rate Sum')
ax2[2].set_xlim((0, 1))

plt.show()

#################Find parameters with positive results
windowSizes=df.windowSize.unique()
trainingSizes=df.trainingSize.unique()
riskRates=df.riskRate.unique()
count=0
for w in windowSizes:
    for t in trainingSizes:
        for r in riskRates:
            df_temp = df[df.windowSize==w][df.trainingSize==t][df.riskRate==r]
            if len(df_temp.index) > 0 and len(df_temp[df.finalBalance>=10000].index)==len(df_temp.index) and len(df_temp[df.finalBalance>10000].index)>0:
                print "%s - %s - %s" %  (w, t, r)
                count += 1
                #df_temp.plot(x='finalDate', y='finalBalance', legend=False)
                
