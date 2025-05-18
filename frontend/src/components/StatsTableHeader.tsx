import { Table, Tooltip } from '@mantine/core';
import { StatsTableColumns } from '../types';

type StatsTableHeaderProps = {
    label: StatsTableColumns;
    selectedColumn: StatsTableColumns;
    isDescending: boolean;
    setSelectedColumn: (column: StatsTableColumns) => void;
    setIsDescending: (desc: boolean | ((prev: boolean) => boolean)) => void;
    overrideDescending?: boolean;
};

export default function StatsTableHeader({
    label,
    setSelectedColumn,
    setIsDescending,
    selectedColumn,
    isDescending,
    overrideDescending = false
}: StatsTableHeaderProps) {
    const isSelected = selectedColumn === label;

    return (
        <Table.Th 
            onClick={() => {
                setSelectedColumn(label);
                setIsDescending((prev) => (isSelected ? !prev : !overrideDescending));
            }}
            style={{
                cursor: "pointer",
                borderTop: isSelected && isDescending ? "4px solid #c4ab6c" : "none",
                borderBottom: isSelected && !isDescending ? "4px solid #c4ab6c" : "none",
                paddingTop: "8px",
                paddingBottom: "8px",
                fontWeight: isSelected ? "700" : "500",
            }}
            className='text-center bg-gray-900 text-lg text-gray-300 hover:text-gray-400'
        >
            {label === "Tier" ? (
                <Tooltip label="Calculated based on win, ban, and pick rate." withArrow>
                    <p className='text-center'>{label}</p>
                </Tooltip>
            ) : label === "Matches" ? (
                <Tooltip label="Minimum of 25 games played." withArrow>
                    <p className='text-center'>{label}</p>
                </Tooltip>
            ) : (
                <p className='text-center'>{label}</p>
            )}
        </Table.Th>
    )
}
