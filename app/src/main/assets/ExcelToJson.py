import json
import pandas as pd


def first(xl):
    data = [{}]
    k = 1
    
    for x in xl.sheet_names:
        print(x)
        if x == 'Лист1':
            df1 = xl.parse(x)
            data = [{} for _ in range(df1.count()['Id'])]
            for y in df1.keys():
                for i, u in enumerate(df1.get(y)):
                    if not pd.isna(u):
                        data[i][y] = u
                    else:
                        data[i][y] = ""
            with open("beasts_out.json", 'w', encoding='utf-8') as w:
                json.dump(data, w, ensure_ascii=False, indent=4)
            k += 1


xl = pd.ExcelFile('beasts.xlsx')
first(xl)
