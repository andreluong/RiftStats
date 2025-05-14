import { Table, Tooltip } from '@mantine/core';
import { ChampionStats, HeaderToChampionStats, Position, StatsTableColumns } from '../types';
import { useState } from 'react';
import StatsTableHeader from './StatsTableHeader';

const tierOrder = ["S", "A", "B", "C", "D"];

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
  championStats = championStats.filter((champion) => champion.matches >= 5);

  // Sort champion stats
  championStats.sort((a, b) => {
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

  const getPositionIcon = (position: Position) => {
    var src = "/roles/inactive/role-fill.png";
    var alt = "Fill";

    switch (position) {
      case "TOP":
        src = "/roles/inactive/role-top.png";
        alt = "Top";
        break;
      case "JUNGLE":
        src = "/roles/inactive/role-jungle.png";
        alt = "Jungle";
        break;
      case "MIDDLE":
        src = "/roles/inactive/role-mid.png";
        alt = "Middle";
        break;
      case "BOTTOM":
        src = "/roles/inactive/role-bot.png";
        alt = "Bottom";
        break;
      case "UTILITY":
        src = "/roles/inactive/role-support.png";
        alt = "Support";
      break;
    }

    return (
      <Tooltip label={alt} withArrow>
        <img src={src} alt={alt} className="w-6 h-6 mx-auto" />
      </Tooltip>
    )
  }

  const rows = championStats.map((champion, index) => (
    <Table.Tr key={champion.name + champion.position}>
      <Table.Td className='w-20'>{index + 1}</Table.Td>
      <Table.Td className='w-80'>{champion.name}</Table.Td>
      <Table.Td className="w-12">{getPositionIcon(champion.position)}</Table.Td>
      <Table.Td className="w-20">{champion.tier}</Table.Td>
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
            <StatsTableHeader
              label="Rank"
              selectedColumn={selectedColumn}
              isDescending={isDescending}
              setSelectedColumn={setSelectedColumn}
              setIsDescending={setIsDescending}
            />
            <StatsTableHeader
              label="Champion"
              selectedColumn={selectedColumn}
              isDescending={isDescending}
              setSelectedColumn={setSelectedColumn}
              setIsDescending={setIsDescending}
              overrideDescending={true}
            />
            <StatsTableHeader
              label="Position"
              selectedColumn={selectedColumn}
              isDescending={isDescending}
              setSelectedColumn={setSelectedColumn}
              setIsDescending={setIsDescending}
            />
            <StatsTableHeader
              label="Tier"
              selectedColumn={selectedColumn}
              isDescending={isDescending}
              setSelectedColumn={setSelectedColumn}
              setIsDescending={setIsDescending}
              overrideDescending={true}
            />
            <StatsTableHeader
              label="Win Rate"
              selectedColumn={selectedColumn}
              isDescending={isDescending}
              setSelectedColumn={setSelectedColumn}
              setIsDescending={setIsDescending}
            />
            <StatsTableHeader
              label="Pick Rate"
              selectedColumn={selectedColumn}
              isDescending={isDescending}
              setSelectedColumn={setSelectedColumn}
              setIsDescending={setIsDescending}
            />
            <StatsTableHeader
              label="Ban Rate"
              selectedColumn={selectedColumn}
              isDescending={isDescending}
              setSelectedColumn={setSelectedColumn}
              setIsDescending={setIsDescending}
            />
            <StatsTableHeader
              label="Matches"
              selectedColumn={selectedColumn}
              isDescending={isDescending}
              setSelectedColumn={setSelectedColumn}
              setIsDescending={setIsDescending}
            />
          </Table.Tr>
        </Table.Thead>
        {!championStatsLoading && championStats && championStats.length > 0 && (
          <Table.Tbody>{rows}</Table.Tbody>
        )}
      </Table>
      {championStatsLoading && <p className='pt-4'>Loading champion stats...</p>}
      {!championStatsLoading && championStats && championStats.length === 0 && <p className='pt-4'>No champion stats available</p>}
      {championStatsError && !championStats&& <p className='pt-4'>Error loading champion stats</p>}
    </> 
  );
}
