import { Menu, Button, Tooltip } from "@mantine/core";
import { RegionCode, regions } from "../../types";

export default function RegionMenu({
  selectedRegionCode,
  setSelectedRegionCode,
}: {
  selectedRegionCode: RegionCode;
  setSelectedRegionCode: (region: RegionCode) => void;
}) {
  return (
    <Menu shadow="md" width={170}>
      <Menu.Target>
        <Tooltip label="Change Region" withArrow>
          <Button
            variant="outline"
            color="white"
            styles={{
              root: {
                width: 80,
                overflow: "hidden",
                textOverflow: "ellipsis",
                whiteSpace: "nowrap",
                "--button-hover-color": "oklch(55.1% 0.027 264.364)",
              },
            }}
          >
            {regions[selectedRegionCode].toUpperCase()}
          </Button>
        </Tooltip>
      </Menu.Target>
      <Menu.Dropdown>
        {Object.entries(regions).map(([key, value]) => (
          <Menu.Item
            key={key}
            onClick={() => setSelectedRegionCode(key as RegionCode)}
          >
            {value.toUpperCase()}
          </Menu.Item>
        ))}
      </Menu.Dropdown>
    </Menu>
  );
}
