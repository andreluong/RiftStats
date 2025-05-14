import { Table } from '@mantine/core';
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
            className='text-center bg-gray-900'
        >
            <p className='text-center'>{label}</p>
        </Table.Th>
    )
}
