export type RegionCode = 
    "all" | "br1" | "eun1" | "euw1" | "jp1" | "kr"
  | "la1" | "la2" | "na1" | "oc1" | "tr1" | "ru"
  | "sea" | "tw2" | "vn2" | "sg2" | "me1";

export type Region =
    "all" | "br" | "eune" | "euw" | "jp" | "kr"
  | "lan" | "las" | "na" | "oce" | "tr" | "ru"
  | "sea" | "tw" | "vn" | "sg" | "me";

export const regions: { [key in RegionCode]: Region } = {
  "all": "all",
  "br1": "br",
  "eun1": "eune",
  "euw1": "euw",
  "jp1": "jp",
  "kr": "kr",
  "la1": "lan",
  "la2": "las",
  "na1": "na",
  "oc1": "oce",
  "tr1": "tr",
  "ru": "ru",
  "sea": "sea",
  "tw2": "tw",
  "vn2": "vn",
  "sg2": "sg",
  "me1": "me"
};

export type Tier = "S" | "A" | "B" | "C" | "D";

export type Position = "TOP" | "JUNGLE" | "MIDDLE" | "BOTTOM" | "UTILITY";

export type Role = "ALL" | Position;

export type ChampionStats = {
  name: string,
  position: Position,
  tier: Tier,
  winRate: number,
  pickRate: number,
  banRate: number,
  matches: number
}

export type GameVersion = {
  id: number;
  patch: string;
}

export type StatsTableColumns = "Rank" | "Champion" | "Position" | "Tier" | "Win Rate" | "Pick Rate" | "Ban Rate" | "Matches";

export const HeaderToChampionStats: Record<string, keyof ChampionStats> = {
  "Champion": "name",
  "Position": "position",
  "Tier": "tier",
  "Win Rate": "winRate",
  "Pick Rate": "pickRate",
  "Ban Rate": "banRate",
  "Matches": "matches"
}