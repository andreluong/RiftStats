import { Menu, Button, Tooltip } from "@mantine/core";

// Not implemented yet

export default function RankMenu() {
  return (
    <Menu shadow="md" width={60}>
      <Menu.Target>
        <Tooltip label="Change Rank" withArrow>
          <Button
            variant="outline"
            color="white"
            styles={{
              root: {
                pointerEvents: "none",
              },
            }}
          >
            CHALLENGER
          </Button>
        </Tooltip>
      </Menu.Target>
    </Menu>
  );
}
