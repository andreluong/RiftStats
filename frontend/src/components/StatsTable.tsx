import { Table, Tooltip } from '@mantine/core';
import { ChampionStats, HeaderToChampionStats, Position, StatsTableColumns, Tier } from '../types';
import { useState } from 'react';
import StatsTableHeader from './StatsTableHeader';

const tierOrder = ["S", "A", "B", "C", "D"];

const tierColorMap: Record<Tier, string> = {
  S: "text-pink-400",
  A: "text-purple-300",
  B: "text-blue-400",
  C: "text-teal-400",
  D: "text-gray-500"
};

const positionIconMap: Record<Position, { src: string; alt: string }> = {
  TOP: { src: "/roles/inactive/role-top.png", alt: "Top"},
  JUNGLE: { src: "/roles/inactive/role-jungle.png", alt: "Jungle"},
  MIDDLE: { src: "/roles/inactive/role-mid.png", alt: "Middle"},
  BOTTOM: { src: "/roles/inactive/role-bot.png", alt: "Bottom"},
  UTILITY: { src: "/roles/inactive/role-support.png", alt: "Support"}
}

const getPositionIcon = (position: Position) => {
  const { src, alt } = positionIconMap[position] || { 
    src: "/roles/inactive/role-fill.png", 
    alt: "Fill"
  };

  return (
    <Tooltip label={alt} withArrow>
      <img src={src} alt={alt} className="w-6 h-6 mx-auto" />
    </Tooltip>
  )
}

const headers: { label: StatsTableColumns; overrideDescending?: boolean }[] = [
  { label: "Rank"},
  { label: "Champion", overrideDescending: true },
  { label: "Position" },
  { label: "Tier", overrideDescending: true },
  { label: "Win Rate" },
  { label: "Pick Rate" },
  { label: "Ban Rate" },
  { label: "Matches" }
]

export default function StatsTable({
  championStats,
  championStatsLoading,
  championStatsError
}: {
  championStats: ChampionStats[];
  championStatsLoading: boolean;
  championStatsError: any;
}) {
  const [selectedColumn, setSelectedColumn] = useState<StatsTableColumns>("Win Rate");
  const [isDescending, setIsDescending] = useState<boolean>(true);

  // Filter out low played stats
  const filterLowPlayedChampionStats = championStats.filter((champion) => champion.matches >= 25);

  // Sort champion stats
  const sortedChampionStats = [...filterLowPlayedChampionStats].sort((a, b) => {
    const key = HeaderToChampionStats[selectedColumn];
    const aValue = a[key];
    const bValue = b[key];

    const compareNumbers = (a: number, b: number) => 
      isDescending ? b - a : a - b;

    const compareStrings = (a: string, b: string) =>
      isDescending ? b.localeCompare(a) : a.localeCompare(b);

    // Compare tiers
    if (selectedColumn === "Tier") {
      const aTierIndex = tierOrder.indexOf(aValue as string);
      const bTierIndex = tierOrder.indexOf(bValue as string);
      return compareNumbers(aTierIndex, bTierIndex);
    }
    // Compare strings
    else if (typeof aValue === "string" && typeof bValue === "string") {
      return compareStrings(aValue, bValue);
    } 
    // Compare numbers
    else {
      if (isNaN(Number(aValue)) || isNaN(Number(bValue))) return 0;
      return compareNumbers(Number(aValue), Number(bValue));
    }  
  })

  const rows = sortedChampionStats.map((champion, index) => (
    <Table.Tr key={champion.name + champion.position} className='text-lg'>
      <Table.Td className='w-20'>{index + 1}</Table.Td>
      <Table.Td className='w-92'>{champion.name}</Table.Td>
      <Table.Td className="w-24">{getPositionIcon(champion.position)}</Table.Td>
      <Table.Td className={`w-20 font-semibold ${tierColorMap[champion.tier] || ""}`}>{champion.tier}</Table.Td>
      <Table.Td>{(champion.winRate * 100).toFixed(2) + "%"}</Table.Td>
      <Table.Td>{(champion.pickRate * 100).toFixed(2) + "%"}</Table.Td>
      <Table.Td>{(champion.banRate * 100).toFixed(2) + "%"}</Table.Td>
      <Table.Td>{champion.matches}</Table.Td>
    </Table.Tr>
  ));

  return (
    <>      
      <Table striped="even" stripedColor='#101828' withTableBorder withColumnBorders verticalSpacing="sm" className='bg-gray-800'>
        <Table.Thead>
          <Table.Tr>
            {headers.map((h) => (
              <StatsTableHeader
                key={h.label}
                label={h.label}
                selectedColumn={selectedColumn}
                isDescending={isDescending}
                setSelectedColumn={setSelectedColumn}
                setIsDescending={setIsDescending}
                overrideDescending={h.overrideDescending}
              />
            ))}
          </Table.Tr>
        </Table.Thead>
        {!championStatsLoading && sortedChampionStats && sortedChampionStats.length > 0 && (
          <Table.Tbody>{rows}</Table.Tbody>
        )}
      </Table>
      {championStatsLoading && <p className='pt-4'>Loading champion stats...</p>}
      {!championStatsLoading && sortedChampionStats && sortedChampionStats.length === 0 && <p className='pt-4'>No champion stats available</p>}
      {championStatsError && !sortedChampionStats&& <p className='pt-4'>Error loading champion stats</p>}
    </> 
  );
}
