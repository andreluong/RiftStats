import './App.css'
import StatsTable from './components/StatsTable'
import PatchMenu from './components/menus/PatchMenu'
import RegionMenu from './components/menus/RegionMenu'
import RankMenu from './components/menus/RankMenu'
import QueueMenu from './components/menus/QueueMenu'
import RoleSelection from './components/RoleSelection'
import { RegionCode, ChampionStats, Role, GameVersion } from './types'
import { useEffect, useState } from 'react'
import useSWR from 'swr';
import { fetcher } from './lib/utils'

function App() {
  const [role, setRole] = useState<Role>("ALL");
  const [patch, setPatch] = useState<string>("15.7");
  const [regionCode, setRegionCode] = useState<RegionCode>("all");
  const [filteredChampionStats, setFilteredChampionStats] = useState<ChampionStats[]>([]);

  const {
    data: numMatches,
    error: numMatchesError,
    isLoading: numMatchesLoading
  } = useSWR<number>(`/api/matches/count`, fetcher);

  const {
      data: gameVersions,
      error: gameVersionsError,
      isLoading: gameVersionsLoading
  } = useSWR<GameVersion[]>(`/api/game-versions/all`, fetcher);

  const {
    data: championStats,
    error: championStatsError,
    isLoading: championStatsLoading
  } = useSWR<ChampionStats[]>(`/api/champion-stats/${regionCode}/${patch}/winrates`, fetcher);

  useEffect(() => {
    if (gameVersions && gameVersions.length > 0) {
      setPatch(gameVersions[0].patch); // Latest patch
    }
  }, [gameVersions]); 

  useEffect(() => {
    if (championStats) {
      if (role === "ALL") {
        setFilteredChampionStats(championStats);
      } else {
        const filtered = championStats.filter((champion) => {
          return champion.position === role;
        });
        setFilteredChampionStats(filtered);
      }
    }
  }, [championStats, role]);

  useEffect(() => {
    if (gameVersionsError) {
      console.error("Error loading patches", gameVersionsError);
    }
  }, [gameVersionsError])

  return (
    <div className='gradient-background text-white min-h-screen'>
      <p className='text-7xl font-bold pt-12 mb-4'>RiftStats</p>
      
      {!numMatchesError && <p>Matches Analyzed: {numMatchesLoading ? ("...") : (numMatches || 0)}</p>}
      {numMatchesError && <p>Matches Analzyed: Unknown</p>}
      
      <div className='mt-10 mx-auto w-4/6 pb-10'>

        <div className='flex flex-row justify-between'>
          <RoleSelection selectedRole={role} setSelectedRole={setRole} />
          <div className='flex flex-row gap-4 py-4'>
            <PatchMenu 
              gameVersions={gameVersions} 
              gameVersionLoading={gameVersionsLoading} 
              selectedPatch={patch} 
              setSelectedPatch={setPatch} 
            />
            <RankMenu />
            <QueueMenu />
            <RegionMenu selectedRegionCode={regionCode} setSelectedRegionCode={setRegionCode} />
          </div>
        </div>

        <StatsTable 
          championStats={filteredChampionStats} 
          championStatsLoading={championStatsLoading} 
          championStatsError={championStatsError} 
        />
      </div>
    </div>
  )
}

export default App
